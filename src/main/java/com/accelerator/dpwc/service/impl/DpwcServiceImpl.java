package com.accelerator.dpwc.service.impl;

import com.accelerator.dpwc.Constants;
import com.accelerator.dpwc.DpoaClient;
import com.accelerator.dpwc.domain.Clock;
import com.accelerator.dpwc.domain.ClockRepository;
import com.accelerator.dpwc.domain.User;
import com.accelerator.dpwc.domain.UserRepository;
import com.accelerator.dpwc.exception.DateParseException;
import com.accelerator.dpwc.service.DpwcService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("dpwcService") @EnableCaching
public class DpwcServiceImpl implements DpwcService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserRepository userRepository;

    @Resource
    private ClockRepository clockRepository;

    @Resource @SuppressWarnings("SpringJavaAutowiringInspection")
    private CacheManager cacheManager;

    @Autowired @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private SecurityProperties security;

    @Override
    public List<Clock> getClocks() {
        String username = getUsername();
        if (security.getUser().getName().equals(username)) {
            return Collections.emptyList();
        }
        User user = userRepository.findOne(username);
        return getClocks(username, user.getPassword());
    }

    @Override
    public void addClock(String dateStr, Integer type) {
        try {
            String username = getUsername();

            Clock.Id id = new Clock.Id();
            id.setUser(new User(username));
            Date date = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
            date = DateUtils.truncate(date, Calendar.DATE);
            id.setDate(date);

            Clock clock = clockRepository.findOne(id);
            Date now = new Date();
            if (clock == null) {
                clock = new Clock(id);
                clock.setCreateUser(username);
                clock.setCreateTime(now);
            }
            clock.setUpdateUser(username);
            clock.setUpdateTime(now);

            clock.setType(type == null ? Constants.CLOCK_TYPE_ALL : type);
            clockRepository.save(clock);
        } catch (ParseException e) {
            throw new DateParseException(e);
        }
    }

    @Override
    public void schedule(boolean isClockIn) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String username = user.getUsername();
            String password = user.getPassword();
            List<Clock> clocks = getClocks(username, password);
            for (Clock clock : clocks) {
                Integer type = clock.getType();
                switch (type) {
                    case Constants.CLOCK_TYPE_ALL:
                        break;
                    case Constants.CLOCK_TYPE_IN:
                        if (isClockIn) break;
                    case Constants.CLOCK_TYPE_OUT:
                        if (!isClockIn) break;
                    case Constants.CLOCK_TYPE_NONE:
                    default:
                        continue;
                }
                if (DateUtils.isSameDay(new Date(), clock.getId().getDate())) {
                    try {
                        if (DpoaClient.clock(username, password, isClockIn)) {
                            logger.info("{}打卡成功！", username);
                        } else {
                            logger.info("{}打卡失败！", username);
                        }
                    } catch (Exception e) {
                        logger.info("{}打卡异常！", username, e);
                    }
                }
            }
        }
    }

    private List<Clock> getClocks(String username, String password) {
        // 根据当前时间获取当月一号和下月一号
        Date now = new Date();
        Date min = DateUtils.truncate(now, Calendar.MONTH);
        Date max = DateUtils.ceiling(now, Calendar.MONTH);
        // 根据当月最大和最小日期获取当月每天日期
        Date tempDate = min;
        List<Date> defaultClockDays = Lists.newArrayList();
        while (tempDate.before(max)) {
            defaultClockDays.add(tempDate);
            tempDate = DateUtils.addDays(tempDate, 1);
        }
        // 移除OA假期
        List<Date> holidays = getHolidaysCache(username);
        if (holidays == null) {
            holidays = DpoaClient.holidays(username, password);
            putHolidaysCache(username, holidays);
        }
        defaultClockDays.removeAll(holidays);
        // 添加默认要打卡
        Map<Date, Clock> tempMap = Maps.newHashMap();
        for (Date defaultClockDay : defaultClockDays) {
            Clock defaultClock = new Clock(new Clock.Id());
            Date date = DateUtils.truncate(defaultClockDay, Calendar.DATE);
            defaultClock.getId().setDate(date);
            User user = new User(username, password);
            defaultClock.getId().setUser(user);
            defaultClock.setType(Constants.CLOCK_TYPE_ALL);
            tempMap.put(date, defaultClock);
        }
        // 添加默认不打卡
        for (Date holidayDay : holidays) {
            Clock holidayClock = new Clock(new Clock.Id());
            Date date = DateUtils.truncate(holidayDay, Calendar.DATE);
            holidayClock.getId().setDate(date);
            User user = new User(username, password);
            holidayClock.getId().setUser(user);
            holidayClock.setType(Constants.CLOCK_TYPE_NONE);
            tempMap.put(date, holidayClock);
        }
        // 添加自定义打卡
        List<Clock> customClocks = clockRepository.findByIdUserUsernameAndIdDateBetween(
                username, min, DateUtils.addDays(max, -1));
        for (Clock customClock : customClocks) {
            Date date = customClock.getId().getDate();
            date = DateUtils.truncate(date, Calendar.DATE);
            customClock.getId().setDate(date);
            tempMap.put(date, customClock);
        }
        // 对打卡结果排序
        List<Clock> clocks = Lists.newArrayList(tempMap.values());
        Collections.sort(clocks, new Comparator<Clock>() {
            @Override
            public int compare(Clock o1, Clock o2) {
                return o1.getId().getDate().compareTo(o2.getId().getDate());
            }
        });
        return clocks;
    }

    protected void putHolidaysCache(String username, List<Date> holidays) {
        String key = username + ":" + DateFormatUtils.format(new Date(), "yyyy-MM");
        cacheManager.getCache("holidays").put(key, holidays);
    }

    @SuppressWarnings("unchecked")
    protected List<Date> getHolidaysCache(String username) {
        String key = username + ":" + DateFormatUtils.format(new Date(), "yyyy-MM");
        return cacheManager.getCache("holidays").get(key, List.class);
    }

    protected String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getPrincipal().toString();
        return username;
    }

}
