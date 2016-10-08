package com.accelerator.dpwc.service.impl;

import com.accelerator.dpwc.Constants;
import com.accelerator.dpwc.DpoaClient;
import com.accelerator.dpwc.domain.Clock;
import com.accelerator.dpwc.domain.ClockRepository;
import com.accelerator.dpwc.domain.User;
import com.accelerator.dpwc.domain.UserRepository;
import com.accelerator.dpwc.exception.DateParseException;
import com.accelerator.dpwc.service.DpwcService;
import com.accelerator.dpwc.util.ScheduleUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("dpwcService") @EnableCaching
public class DpwcServiceImpl implements DpwcService, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile ScheduledExecutorService scheduledExecutorService;

    @Autowired @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private SecurityProperties security;

    @Resource
    private ClockRepository clockRepository;

    @Resource @SuppressWarnings("SpringJavaAutowiringInspection")
    private CacheManager cacheManager;

    @Resource
    private UserRepository userRepository;

    @Override @Transactional(rollbackFor = Throwable.class)
    public void addUser(String username, String password) {
        User user = userRepository.findOne(username);
        Date nowDate = new Date();
        if (user == null) {
            user = new User(username);
            user.setCreateUser(username);
            user.setCreateTime(nowDate);
        }
        user.setUpdateUser(username);
        user.setUpdateTime(nowDate);
        user.setPassword(password);
        userRepository.save(user);
        resizeScheduledThreadPool();
    }

    @Override
    public void delUser(String username) {
        userRepository.delete(username);
    }

    @Override @Transactional(rollbackFor = Throwable.class)
    public void addClock(String dateStr, Integer type) {
        try {
            if (StringUtils.isEmpty(dateStr)) {
                return;
            }
            Date date = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
            String username = getCurrentUsername();
            Clock.Id clockId = new Clock.Id(
                    DateUtils.truncate(date, Calendar.DATE));
            clockId.setUser(new User(username));
            Clock clock = clockRepository.findOne(clockId);
            Date nowDate = new Date();
            if (clock == null) {
                clock = new Clock(clockId);
                clock.setCreateUser(username);
                clock.setCreateTime(nowDate);
            }
            clock.setUpdateUser(username);
            clock.setUpdateTime(nowDate);
            clock.setType(type == null ? Constants.CLOCK_TYPE_ALL : type);
            clockRepository.save(clock);
        } catch (ParseException e) {
            throw new DateParseException(e);
        }
    }

    @Override @Transactional(rollbackFor = Throwable.class)
    public void addClocks(Map<String, Integer> params) {
        for (Map.Entry<String, Integer> param : params.entrySet()) {
            addClock(param.getKey(), param.getValue());
        }
    }

    @Override @Transactional(readOnly = true)
    public List<Clock> getClocks(String dateStr) {
        try {
            String username = getCurrentUsername();
            if (security.getUser().getName().equals(username)) {
                return Collections.emptyList();
            }
            Date monthDate = StringUtils.isEmpty(dateStr) ?
                    new Date() : DateUtils.parseDate(dateStr, "yyyy-MM");
            User user = userRepository.findOne(username);
            String password = user.getPassword();
            return getClocks(username, password, monthDate);
        } catch (ParseException e) {
            throw new DateParseException(e);
        }
    }

    @Override @Transactional(readOnly = true)
    public void schedule(final boolean isClockIn) {
        for (final User user : userRepository.findAll()) {
            int delay = ScheduleUtils.randomSecondWithTenMinutes();
            // 10分钟内随机延迟
            scheduledExecutorService.schedule(new Runnable() {
                @Override public void run() {
                    doSchedule(user, isClockIn);
                }
            }, delay, TimeUnit.SECONDS);
        }
    }

    private void doSchedule(User user, boolean isClockIn) {
        String username = user.getUsername();
        String password = user.getPassword();
        Date nowDate = new Date();
        for (Clock clock : getClocks(username, password, nowDate)) {
            switch (clock.getType()) {
                case Constants.CLOCK_TYPE_ALL:
                    break;
                case Constants.CLOCK_TYPE_OUT:
                    if (!isClockIn) break;
                case Constants.CLOCK_TYPE_IN:
                    if (isClockIn) break;
                default:
                    continue;
            }
            if (DateUtils.isSameDay(nowDate, clock.getId().getDate())) {
                if (DpoaClient.clock(username, password, isClockIn)) {
                    logger.info("{}打卡成功！", username);
                } else {
                    logger.info("{}打卡失败！", username);
                }
            }
        }
    }

    protected List<Clock> getClocks(String username, String password, Date monthDate) {
        // 根据参数月份获取此月一号和下月一号
        Date minDate = DateUtils.truncate(monthDate, Calendar.MONTH);
        Date maxDate = DateUtils.ceiling(monthDate, Calendar.MONTH);
        // 根据此月份最大和最小日期获取此月每天日期
        List<Date> clockDates = Lists.newArrayList(minDate);
        Date tempDate = minDate;
        while (tempDate.before(maxDate)) {
            tempDate = DateUtils.addDays(tempDate, 1);
            clockDates.add(tempDate);
        }
        // 移除OA假期
        List<Date> holidayDates = getHolidayDates(username, password, monthDate);
        clockDates.removeAll(holidayDates);
        // 添加默认要打卡
        Map<Date, Clock> tempMap = Maps.newHashMapWithExpectedSize(clockDates.size());
        for (Date clockDate : clockDates) {
            Date date = DateUtils.truncate(clockDate, Calendar.DATE);
            Clock.Id clockId = new Clock.Id(date);
            clockId.setUser(new User(username, password));
            Clock clock = new Clock(clockId);
            clock.setType(Constants.CLOCK_TYPE_ALL);
            tempMap.put(date, clock);
        }
        // 添加默认不打卡
        for (Date holidayDate : holidayDates) {
            Date date = DateUtils.truncate(holidayDate, Calendar.DATE);
            Clock.Id clockId = new Clock.Id(date);
            clockId.setUser(new User(username, password));
            Clock clock = new Clock(clockId);
            clock.setType(Constants.CLOCK_TYPE_NONE);
            tempMap.put(date, clock);
        }
        // 添加自定义打卡
        List<Clock> clocks = clockRepository.findByIdUserUsernameAndIdDateBetween(
                username, minDate, DateUtils.addDays(maxDate, -1));
        for (Clock clock : clocks) {
            Clock.Id clockId = clock.getId();
            Date date = DateUtils.truncate(clockId.getDate(), Calendar.DATE);
            clockId.setDate(date);
            tempMap.put(date, clock);
        }
        // 对打卡结果排序
        List<Clock> result = Lists.newArrayList(tempMap.values());
        Collections.sort(result, new Comparator<Clock>() {
            @Override
            public int compare(Clock clock1, Clock clock2) {
                Date clock1Date = clock1.getId().getDate();
                Date clock2Date = clock2.getId().getDate();
                return clock1Date.compareTo(clock2Date);
            }
        });
        return result;
    }

    protected List<Date> getHolidayDates(String username, String password, Date monthDate) {
        String cacheKey = username + ":" + DateFormatUtils.format(monthDate, "yyyy-MM");
        @SuppressWarnings("unchecked")
        List<Date> result = cacheManager.getCache("holidays").get(cacheKey, List.class);
        if (result == null) {
            result = DpoaClient.holidays(username, password);
            cacheManager.getCache("holidays").put(cacheKey, result);
        }
        return result;
    }

    protected String getCurrentUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getPrincipal().toString();
    }

    public void resizeScheduledThreadPool() {
        scheduledExecutorService = Executors
                .newScheduledThreadPool((int) userRepository.count());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        resizeScheduledThreadPool();
    }
}