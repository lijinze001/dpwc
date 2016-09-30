package com.accelerator;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class MainTest {

    public static void main(String[] args) {
        Date now = new Date();
        Date min = DateUtils.truncate(now, Calendar.MONTH);
        Date max = DateUtils.ceiling(now, Calendar.MONTH);

        System.out.println(min.before(max));

        System.out.println(DateFormatUtils.format(max, "yyyy-MM-dd HH:mm:ss"));
    }

    private static void sayDate(Date date) {
        System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

}
