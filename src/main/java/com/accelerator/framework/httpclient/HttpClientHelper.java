package com.accelerator.framework.httpclient;

import com.accelerator.framework.spring.ApplicationContextHolder;
import com.google.common.collect.Lists;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class HttpClientHelper {

    public static final String HTTP_CLIENT_BEAN_NAME = "httpClient";

    public static ResponseData doGet(String url) {
        return doGet(url, null, null, null);
    }

    public static ResponseData doGet(String url, Map<String, String> params) {
        return doGet(url, null, null, params);
    }

    public static ResponseData doGet(String url, List<Header> headers, List<Cookie> cookies,
            Map<String, String> params) {
        // 构建GET请求
        if (!CollectionUtils.isEmpty(params)) {
            url = formatUrl(url, params);
        }
        HttpGet request = new HttpGet(url);
        // 设置请求头
        setHeaders(request, headers);
        // 构建上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(convertCookies(cookies));
        // 执行请求
        return executeRequest(request, context);
    }

    public static ResponseData doPost(String url, String jsonStr) {
        return doPost(url, null, null, jsonStr);
    }

    public static ResponseData doPost(String url, List<Header> headers, List<Cookie> cookies,
            String jsonStr) {
        // 创建POST请求
        HttpPost request = new HttpPost(url);
        // 设置请求头
        setHeaders(request, headers);
        // 构建上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(convertCookies(cookies));
        // 设置请求参数
        HttpEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        // 执行请求
        return executeRequest(request, context);
    }

    public static ResponseData doPost(String url) {
        Map<String, String> params = Collections.emptyMap();
        return doPost(url, null, null, params);
    }

    public static ResponseData doPost(String url, Map<String, String> params) {
        return doPost(url, null, null, params);
    }

    public static ResponseData doPost(String url, List<Header> headers, List<Cookie> cookies,
            Map<String, String> params) {
        // 创建POST请求
        HttpPost request = new HttpPost(url);
        // 设置请求头
        setHeaders(request, headers);
        // 构建上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(convertCookies(cookies));
        // 设置请求参数
        if (!CollectionUtils.isEmpty(params)) {
            HttpEntity entity = new UrlEncodedFormEntity(parseParams(params), Consts.UTF_8);
            request.setEntity(entity);
        }
        // 执行请求
        return executeRequest(request, context);
    }

    public static void addHeaders(HttpRequestBase request, List<Header> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            for (Header header : headers) {
                request.addHeader(header);
            }
        }
    }

    public static void setHeaders(HttpRequestBase request, List<Header> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            for (Header header : headers) {
                request.setHeader(header);
            }
        }
    }

    public static CookieStore convertCookies(List<Cookie> cookies) {
        CookieStore cookieStore = new BasicCookieStore();
        if (!CollectionUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                cookieStore.addCookie(cookie);
            }
        }
        return cookieStore;
    }

    public static List<NameValuePair> parseParams(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return Collections.emptyList();
        }
        List<NameValuePair> nameValuePairs = Lists.newArrayListWithCapacity(params.size());
        for (Map.Entry<String, String> param : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        return nameValuePairs;
    }

    public static String formatParams(Map<String, String> params) {
        return formatParams(params, Consts.UTF_8);
    }

    public static String formatParams(Map<String, String> params, Charset charset) {
        if (CollectionUtils.isEmpty(params)) {
            return "";
        }
        return URLEncodedUtils.format(parseParams(params), charset);
    }

    public static String formatUrl(String url, Map<String, String> params) {
        return formatUrl(url, params, Consts.UTF_8);
    }

    public static String formatUrl(String url, Map<String, String> params, Charset charset) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        url = url.endsWith("?") ? url : url.concat("?");
        return url.concat(formatParams(params, charset));
    }

    public static CloseableHttpClient getHttpClient() {
        ApplicationContext applicationContext = ApplicationContextHolder.getRequiredApplicationContext();
        return applicationContext.getBean(HTTP_CLIENT_BEAN_NAME, CloseableHttpClient.class);
    }

    public static ResponseData executeRequest(HttpRequestBase request, HttpContext context) {
        Assert.notNull(request, "request is required; it must not be null");
        if (context == null) { context = HttpClientContext.create(); }
        CloseableHttpClient httpClient = getHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request, context);
            return new ResponseData(response, context);
        } catch (ClientProtocolException e) {
            String messageFormat = "执行请求：[%s]；发生ClientProtocolException！";
            throw new HttpClientException(String.format(messageFormat, request), e);
        } catch (IOException e) {
            String messageFormat = "执行请求：[%s]；发生IOException！";
            throw new HttpClientException(String.format(messageFormat, request), e);
        } finally {
            request.releaseConnection();
            HttpClientUtils.closeQuietly(response);
            ApplicationContext applicationContext = ApplicationContextHolder.getRequiredApplicationContext();
            if (!applicationContext.isSingleton(HTTP_CLIENT_BEAN_NAME)) {
                HttpClientUtils.closeQuietly(httpClient);
            }
        }
    }

}
