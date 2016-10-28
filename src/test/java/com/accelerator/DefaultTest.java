package com.accelerator;

import com.accelerator.framework.util.DateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DefaultTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void switchTest() throws Exception {
        int condition = 1;
        switch (condition) {
            case 1:
                logger.info("case 1");
            default:
                logger.info("default");
        }
    }

    @Test
    public void dateTest() throws Exception {
        Date monthDate = DateUtils.createNow();
        // 根据参数月份获取此月一号和下月一号
        Date minDate = DateUtils.truncate(monthDate, Calendar.MONTH);
        Date maxDate = DateUtils.ceiling(monthDate, Calendar.MONTH);
        // 根据此月份最大和最小日期获取此月每天日期
        List<Date> clockDates = Lists.newArrayList(minDate);
        Date tempDate = minDate;
        while ((tempDate = DateUtils.addDays(tempDate, 1)).before(maxDate)) {
            clockDates.add(tempDate);
        }
        for (Date clockDate : clockDates) {
            String clockDateStr = DateFormatUtils.format(clockDate, "yyyy-MM-dd HH:mm:ss");
            logger.info("ClockDate：{}", clockDateStr);
        }
    }
}
