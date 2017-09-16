package com.accelerator.dpwc.util;

import org.apache.commons.lang3.RandomUtils;

public abstract class ScheduleUtils {

    public static int randomSecondWithMinutes(int minutes) {
        return RandomUtils.nextInt(0, minutes * 60 + 1);
    }

}
