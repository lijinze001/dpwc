package com.accelerator.dpwc.service;

import com.accelerator.dpwc.domain.Clock;

import java.util.List;

public interface DpwcService {

    List<Clock> getClocks();

    void addClock(String dateStr, Integer type);

    void schedule(boolean isClockIn);

}
