package com.accelerator.framework.httpclient;

import com.accelerator.dpwc.DpoaClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpClientHelperTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void doGet() throws Exception {
        String username = "318898";
        String password = "C@763124";
        logger.info(String.valueOf(DpoaClient.holidays(username, password, "2016-9")));
    }

}