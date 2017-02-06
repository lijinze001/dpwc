package com.accelerator.framework.exception;

import com.accelerator.framework.util.ExceptionUtils;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 746126594234925038L;

    private int code = ErrorCode.SERVER_ERROR;

    private Object args[];

    private boolean logStackTrace = true;

    private String renderedMessage;

    public CustomException() {}

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(int code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public CustomException(int code, Throwable cause) {
        this(cause);
        this.code = code;
    }

    public CustomException(int code, Throwable cause, Object... args) {
        super(cause);
        this.code = code;
        this.args = args;
    }

    public CustomException(int code, String defaultMessage, Object... args) {
        super(defaultMessage);
        this.code = code;
        this.args = args;
    }

    public CustomException(int code, String defaultMessage, Throwable cause, Object... args) {
        super(defaultMessage, cause);
        this.code = code;
        this.args = args;
    }

    public int getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getMessage() {
        if (renderedMessage == null) {
            renderedMessage = ExceptionUtils.buildMessage(code, args, super.getMessage(), getCause());
        }
        return renderedMessage;
    }

    public static CustomException fromRoot(Exception e) {
        return new CustomException(ExceptionUtils.getRootCause(e));
    }

    public void setRenderedMessage(String renderedMessage) {
        this.renderedMessage = renderedMessage;
    }

    public boolean isLogStackTrace() {
        return logStackTrace;
    }

    public void setLogStackTrace(boolean logStackTrace) {
        this.logStackTrace = logStackTrace;
    }

    public CustomException loggable(boolean loggable) {
        this.setLogStackTrace(loggable);
        return this;
    }


}
