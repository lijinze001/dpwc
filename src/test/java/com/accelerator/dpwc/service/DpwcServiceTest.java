package com.accelerator.dpwc.service;

import com.accelerator.dpwc.domain.Clock;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DpwcServiceTest {

    @Resource
    private DpwcService dpwcService;

    @Test
    public void addUser() throws Exception {

    }

    @Test
    public void delUser() throws Exception {

    }

    @Test
    public void getClocks() throws Exception {
        List<Clock> clocks = dpwcService.getClocks("2016-09");
        System.out.println(clocks);
    }

    @Test
    public void addClock() throws Exception {

    }

    @Test
    public void addClocks() throws Exception {

    }

    @Test
    public void schedule() throws Exception {
      
    }

}