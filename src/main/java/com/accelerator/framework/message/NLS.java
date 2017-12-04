package com.accelerator.framework.message;

import com.accelerator.framework.spring.ApplicationContextHolder;
import org.springframework.context.ApplicationContext;

import java.util.Locale;

public abstract class NLS {

    public static final String MESSAGE_PROVIDER_BEAN_NAME = "messageProvider";

    public static String getMessage(String code) {
        return getMessageProvider().getMessage(code);
    }

    public static String getMessage(String code, Object[] args) {
        return getMessageProvider().getMessage(code, args);
    }

    public static String getMessage(String code, Object[] args, String defaultMessage) {
        return getMessageProvider().getMessage(code, args, defaultMessage);
    }

    public static String getMessage(String code, Object[] args, Locale locale) {
        return getMessageProvider().getMessage(code, args, locale);
    }

    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return getMessageProvider().getMessage(code, args, defaultMessage, locale);
    }

    public static MessageProvider getMessageProvider() {
        ApplicationContext applicationContext = ApplicationContextHolder.getRequiredApplicationContext();
        return applicationContext.getBean(MESSAGE_PROVIDER_BEAN_NAME, MessageProvider.class);
    }

}
