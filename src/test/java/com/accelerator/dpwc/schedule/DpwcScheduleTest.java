package com.accelerator.dpwc.schedule;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Accelerator on 2016/9/30.
 */
public class DpwcScheduleTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void test() throws InterruptedException {
        executorService.schedule(new Runnable() {
            @Override public void run() {
                logger.info("已执行!");
            }
        }, 3, TimeUnit.SECONDS);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void name() throws Exception {
        while (true)
            logger.info(String.valueOf(20 * 60 +1));

    }
}