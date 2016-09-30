package com.accelerator.dpwc.domain;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserRepository userRepository;

    @Test
    public void test01_sava() {
        User user = new User();
        Date now = new Date();
        user.setUsername("318898");
        user.setPassword("xxxxxx");
        user.setCreateTime(now);
        user.setCreateUser("318898");
        user.setUpdateTime(now);
        user.setUpdateUser("318898");
        userRepository.save(user);
        logger.info(user.toString());
    }

    @Test
    public void test02_find() {
        User user = userRepository.findOne("318898");
        logger.info(String.valueOf(user));
    }

    @Test
    public void test03_get() {
        User user = userRepository.getOne("318898");
        logger.info(String.valueOf(user));
    }

    @Test
    public void test04_deletes() {
        userRepository.delete("318898");
    }


}
