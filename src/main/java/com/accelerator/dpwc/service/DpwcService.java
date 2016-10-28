package com.accelerator.dpwc.service;

import com.accelerator.dpwc.domain.Clock;
import com.accelerator.dpwc.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DpwcService {

    void addUser(String username, String password);

    void delUser(String username);

    Page<User> getUsers(Integer pageNum);

    User getUser(String username);

    List<Clock> getClocks(String dateStr);

    void addClock(String dateStr, Integer type);

    void addClocks(Map<String, Integer> params);

    void schedule(boolean isClockIn);

}
