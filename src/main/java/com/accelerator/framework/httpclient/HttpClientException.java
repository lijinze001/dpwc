package com.accelerator.framework.httpclient;

public class HttpClientException extends RuntimeException {

    private static final long serialVersionUID = 8193512193122326664L;

    public HttpClientException() {
        super();
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }

    protected HttpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
