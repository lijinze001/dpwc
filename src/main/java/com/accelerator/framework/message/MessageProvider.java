package com.accelerator.framework.message;

import java.util.Locale;

public interface MessageProvider {

    String getMessage(String code);

    String getMessage(String code, Object[] args);

    String getMessage(String code, Object[] args, String defaultMessage);

    String getMessage(String code, Object[] args, Locale locale);

    String getMessage(String code, Object[] args, String defaultMessage, Locale locale);

}
