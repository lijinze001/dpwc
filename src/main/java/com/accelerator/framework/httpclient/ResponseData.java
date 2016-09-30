package com.accelerator.framework.httpclient;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class ResponseData implements Serializable {

    private static final long serialVersionUID = -1285292747469978180L;

    private int statusCode;

    private String mimeType;

    private Charset charset;

    private List<Header> headers;

    private byte[] contentBytes;

    private List<Cookie> cookies;

    ResponseData(HttpResponse response, HttpContext context) {
        Asserts.notNull(response, "response");
        // 获取请求状态
        StatusLine statusLine = response.getStatusLine();
        statusCode = statusLine.getStatusCode();
        // 获取头信息
        Header[] headerArr = response.getAllHeaders();
        headers = Arrays.asList(headerArr);
        // 获取实体
        HttpEntity entity = response.getEntity();
        // 获取内容
        try {
            contentBytes = EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取ContentType并分解为相关信息
        ContentType contentType = ContentType.get(entity);
        if (contentType != null) {
            mimeType = contentType.getMimeType();
            charset = contentType.getCharset();
        }
        // 获取cookie信息
        if (context != null) {
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            CookieStore cookieStore = clientContext.getCookieStore();
            cookies = cookieStore.getCookies();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getContentStr() {
        if (charset == null) {
            return getContentStr(Consts.UTF_8);
        } else {
            return getContentStr(charset);
        }
    }

    public String getContentStr(String charsetName) {
        Charset charset;
        if (Charset.isSupported(charsetName)) {
            charset = Charset.forName(charsetName);
        } else {
            charset = null;
        }
        return getContentStr(charset);
    }

    public String getContentStr(Charset charset) {
        if (charset == null) {
            return new String(contentBytes, Consts.UTF_8);
        } else {
            return new String(contentBytes, charset);
        }
    }

    @Override
    public String toString() {
        return getContentStr();
    }

}
