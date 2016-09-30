package com.accelerator.framework.util;

import com.accelerator.framework.exception.CustomException;
import com.accelerator.framework.exception.ErrorCode;
import com.accelerator.framework.message.NLS;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;

import java.util.Set;

public abstract class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {

    public static String buildMessage(int code, Object[] args, String defaultMessage, Throwable cause) {
        String message = StringUtils.EMPTY;
        if (code != ErrorCode.SERVER_ERROR) {
            message = NLS.getMessage("error." + code, args);
        }
        if (defaultMessage != null) {
            message = (message == StringUtils.EMPTY) ?
                    defaultMessage : (message + "; " + defaultMessage);
        }
        StringBuilder renderedMessage = new StringBuilder(message);
        Set<Throwable> throwables = Sets.newHashSet(getThrowables(cause));
        for (Throwable throwable : throwables) {
            if (throwable instanceof CustomException || throwable instanceof NestedRuntimeException) {
                continue;
            }
            String nestedMessage = NestedExceptionUtils.buildMessage(StringUtils.EMPTY, throwable);
            renderedMessage.append(nestedMessage);
        }
        return renderedMessage.toString();
    }

}
