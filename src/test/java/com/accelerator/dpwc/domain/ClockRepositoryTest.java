package com.accelerator.dpwc.domain;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClockRepositoryTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ClockRepository clockRepository;

    @Test
    public void save() throws Exception {
        Date now = new Date();
        Clock clock = new Clock();
        clock.setType(1);
        clock.setCreateTime(now);
        clock.setCreateUser("318898");
        clock.setUpdateTime(now);
        clock.setUpdateUser("318898");
        User user = new User("318898");
        clock.setId(new Clock.Id(user, now));
        clockRepository.save(clock);
        logger.info(clock.toString());
    }


    @Test
    public void findByUsernameAndTime() throws Exception {
        Date now = new Date();
        Date min = DateUtils.truncate(now, Calendar.MONTH);
        Date max = DateUtils.ceiling(now, Calendar.MONTH);
        List<Clock> clocks = clockRepository.findByIdUserUsernameAndIdDateBetween("318898", min, max);
        for (Clock clock : clocks) {
            Date date = clock.getId().getDate();
            logger.info(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        }
    }

}