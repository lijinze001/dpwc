package com.accelerator.framework.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationContextHolder implements ApplicationListener<ContextRefreshedEvent> {

    private static ApplicationContextHolder instance;

    private static ApplicationContext applicationContext;

    public static ApplicationContext getRequiredApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "ApplicationContextHolder instance [" + instance + "] does not run in an ApplicationContext");
        }
        return getApplicationContext();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        applicationContext = event.getApplicationContext();
        instance = this;
    }

}

