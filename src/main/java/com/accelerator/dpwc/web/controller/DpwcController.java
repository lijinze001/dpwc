package com.accelerator.dpwc.web.controller;

import com.accelerator.dpwc.service.DpwcService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller
public class DpwcController {

    @Resource
    private DpwcService dpwcService;

    @RequestMapping(path = "home", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("current", "home");
        return "view";
    }

    @RequestMapping(path = "schedule", method = RequestMethod.GET)
    public String schedule(String dateStr, Model model) {
        model.addAttribute("current", "schedule");
        model.addAttribute("clocks", dpwcService.getClocks(dateStr));
        return "view";
    }

    @RequestMapping(path = "schedule", method = RequestMethod.POST)
    public String schedule(String date, Integer type) {
        dpwcService.addClock(date, type);
        return "redirect:/schedule";
    }

    @RequestMapping(path = "about", method = RequestMethod.GET)
    public String about(Model model) {
        model.addAttribute("current", "about");
        return "view";
    }


}
