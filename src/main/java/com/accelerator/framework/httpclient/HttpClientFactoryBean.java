package com.accelerator.framework.httpclient;

import org.apache.http.Consts;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class HttpClientFactoryBean extends AbstractFactoryBean<CloseableHttpClient> {

    private int poolMaxTotal = 20;

    private int poolDefaultMaxPerRoute = poolMaxTotal;

    private int maxConnPerRoute = poolMaxTotal;

    private int requestRetryCount = 3;

    private int connectTimeout = 1000;

    private int connectionRequestTimeout = 1000;

    private int socketTimeout = 5000;

    private HttpClientBuilder httpClientBuilder;

    @Override
    protected CloseableHttpClient createInstance() throws Exception {
        return httpClientBuilder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return CloseableHttpClient.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createHttpClientBuilder();
        super.afterPropertiesSet();
    }

    private void createHttpClientBuilder() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) {
                return true;
            }
        });

        ConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(),
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();

        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(-1)
                .setMaxLineLength(-1)
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();

        connectionManager.setMaxTotal(poolMaxTotal);
        connectionManager.setDefaultMaxPerRoute(poolDefaultMaxPerRoute);
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        Registry<CookieSpecProvider> cookieSpecRegistry = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                .register(CookieSpecs.STANDARD, new RFC6265CookieSpecProvider())
                .register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecProvider())
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setConnectTimeout(connectTimeout)// 和服务器建立连接的timeout
                .setConnectionRequestTimeout(connectionRequestTimeout)// 从连接池获取连接的timeout
                .setSocketTimeout(socketTimeout)// 从服务器读取数据的timeout
                .build();

        // 请求异常重试次数
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(requestRetryCount, true);

        httpClientBuilder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                .setDefaultCookieSpecRegistry(cookieSpecRegistry)
                .setMaxConnPerRoute(maxConnPerRoute);
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public void setPoolDefaultMaxPerRoute(int poolDefaultMaxPerRoute) {
        this.poolDefaultMaxPerRoute = poolDefaultMaxPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public void setRequestRetryCount(int requestRetryCount) {
        this.requestRetryCount = requestRetryCount;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

}
