package com.accelerator.framework.spring.boot.autoconfigure.httpclient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "httpclient")
public class HttpClientProperties {

    private int poolMaxTotal = 20;

    private int poolDefaultMaxPerRoute = poolMaxTotal;

    private int maxConnPerRoute = poolMaxTotal;

    private int requestRetryCount = 3;

    private int connectTimeout = 1000;

    private int connectionRequestTimeout = 1000;

    private int socketTimeout = 5000;

    private boolean singleton = true;

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolDefaultMaxPerRoute() {
        return poolDefaultMaxPerRoute;
    }

    public void setPoolDefaultMaxPerRoute(int poolDefaultMaxPerRoute) {
        this.poolDefaultMaxPerRoute = poolDefaultMaxPerRoute;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public int getRequestRetryCount() {
        return requestRetryCount;
    }

    public void setRequestRetryCount(int requestRetryCount) {
        this.requestRetryCount = requestRetryCount;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
