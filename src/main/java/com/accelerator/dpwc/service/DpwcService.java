package com.accelerator.dpwc.service;

import com.accelerator.dpwc.domain.Clock;
import com.accelerator.dpwc.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DpwcService {

    void addUser(User user);

    void addUser(String username, String password);

    void delUser(String username);

    List<User> getUsers();

    Page<User> getUserPage(Integer pageNum);

    void addClock(String dateStr, Integer type);

    void addClocks(Map<String, Integer> params);

    List<Clock> getClocks(String dateStr);

    void clock(String username, String password, boolean isClockIn);

    void clockAll(boolean isClockIn);

    void downUserNickname(User user);

    void downHolidays(User user);

}
