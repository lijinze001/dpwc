package com.accelerator;

import com.accelerator.framework.util.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

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
        Date sameMonth = DateUtils.createNow();
        // 通过下个月第一天判断是否需要下拉下个月的假期
        Date nextMonth = DateUtils.ceiling(sameMonth, Calendar.MONTH);
        logger.info("nextMonth:{}", DateFormatUtils.format(nextMonth, "yyyy-MM-dd"));
        logger.info("DateUtils.addDays(nextMonth, -6):{}", DateFormatUtils.format(DateUtils.addDays(nextMonth, -6), "yyyy-MM-dd"));
        if (DateUtils.addDays(nextMonth, -6).before(sameMonth)) {
            logger.info("update");
        }
    }
}
