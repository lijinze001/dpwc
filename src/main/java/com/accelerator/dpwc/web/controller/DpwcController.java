package com.accelerator.dpwc.web.controller;

import com.accelerator.dpwc.domain.Clock;
import com.accelerator.dpwc.domain.User;
import com.accelerator.dpwc.service.DpwcService;
import com.accelerator.framework.util.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
public class DpwcController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DpwcService dpwcService;

    @RequestMapping(path = "home", method = RequestMethod.GET)
    public String home(Model model) {
        initModel(model);
        model.addAttribute("current", "home");
        return "view";
    }

    @RequestMapping(path = "schedule", method = RequestMethod.GET)
    public String schedule(String dateStr, Model model) {
        initModel(model);
        model.addAttribute("current", "schedule");
        List<Clock> clocks = dpwcService.getClocks(dateStr);
        model.addAttribute("clocks", clocks);
        return "view";
    }

    @RequestMapping(path = "schedule", method = RequestMethod.POST)
    public String schedule(String date, Integer type) {
        dpwcService.addClock(date, type);
        return "redirect:/schedule";
    }

    @RequestMapping(path = "admin", method = RequestMethod.GET)
    public String admin(Integer pageNum, Model model) {
        model.addAttribute("current", "admin");
        Page<User> userPage = dpwcService.getUsers(pageNum);
        model.addAttribute("userPage", userPage);
        return "view";
    }

    @RequestMapping(path = "admin", method = RequestMethod.POST)
    public String admin(String username) {
        dpwcService.delUser(username);
        return "redirect:/admin";
    }

    @RequestMapping(path = "about", method = RequestMethod.GET)
    public String about(Model model) {
        initModel(model);
        model.addAttribute("current", "about");
        return "view";
    }

    private void initModel(Model model) {
        Date sameMonth = DateUtils.createNow();
        Date nextMonth = DateUtils.addMonths(sameMonth, 1);
        String sameMonthStr = DateFormatUtils.format(sameMonth, "yyyy-MM");
        String nextMonthStr = DateFormatUtils.format(nextMonth, "yyyy-MM");
        model.addAttribute("sameMonth", sameMonthStr);
        model.addAttribute("nextMonth", nextMonthStr);
    }


}
