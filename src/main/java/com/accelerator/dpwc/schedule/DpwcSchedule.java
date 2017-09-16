package com.accelerator.dpwc.schedule;

import com.accelerator.dpwc.domain.User;
import com.accelerator.dpwc.service.DpwcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Configuration @EnableScheduling @ConfigurationProperties(prefix = "schedule")
public class DpwcSchedule {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DpwcService dpwcService;

    private int clockOutOffsetMinutes;

    private int clockInOffsetMinutes;

    @Scheduled(cron = "${schedule.clock-out-cron}")
    public void clockOutScheduler() {
        logger.info("开始执行下班打卡");
        dpwcService.clockAll(false, clockOutOffsetMinutes);
        logger.info("结束执行下班打卡");
    }

    @Scheduled(cron = "${schedule.clock-in-cron}")
    public void clockInScheduler() {
        logger.info("开始执行上班打卡");
        dpwcService.clockAll(true, clockInOffsetMinutes);
        logger.info("结束执行上班打卡");
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void downHolidaysScheduler() {
        for (User user : dpwcService.getUsers()) {
            dpwcService.downHolidays(user);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void downNicknameScheduler() {
        for (User user : dpwcService.getUsers()) {
            dpwcService.downUserNickname(user);
        }
    }

    public void setClockOutOffsetMinutes(int clockOutOffsetMinutes) {
        this.clockOutOffsetMinutes = clockOutOffsetMinutes;
    }

    public void setClockInOffsetMinutes(int clockInOffsetMinutes) {
        this.clockInOffsetMinutes = clockInOffsetMinutes;
    }
}
