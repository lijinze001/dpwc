package com.accelerator.framework.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static Date createNow() {
        Locale locale = LocaleContextHolder.getLocale();
        Calendar calendar = Calendar.getInstance(locale);
        return calendar.getTime();
    }

}
