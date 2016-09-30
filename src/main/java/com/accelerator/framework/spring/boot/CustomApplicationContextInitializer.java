package com.accelerator.framework.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContextInitializer;

public class CustomApplicationContextInitializer implements ApplicationContextInitializer<EmbeddedWebApplicationContext> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initialize(EmbeddedWebApplicationContext applicationContext) {
        /*
        本想定义不允许同名Bean覆盖 降低系统复杂程度
        但是spingboot中actuator模块有意重定义security模块的Bean
        不得已定义为允许同名Bean覆盖
         */
        applicationContext.setAllowBeanDefinitionOverriding(true);

        /*
        本想定义不允许循环引用 降低系统复杂程度
        但是spingboot中autoconfigure结构太复杂 自身也有循环引用
        不得已定义为允许循环引用
         */
        applicationContext.setAllowCircularReferences(true);
    }

}
