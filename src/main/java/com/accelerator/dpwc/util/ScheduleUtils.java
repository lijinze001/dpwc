package com.accelerator.dpwc.util;

import org.apache.commons.lang3.RandomUtils;

public abstract class ScheduleUtils {

    public static int randomSecondWithTenMinutes() {
        return RandomUtils.nextInt(0, 10 * 60 + 1);
    }

}
