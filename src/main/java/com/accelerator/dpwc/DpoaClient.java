package com.accelerator.dpwc;

import com.accelerator.framework.httpclient.HttpClientHelper;
import com.accelerator.framework.httpclient.ResponseData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class DpoaClient {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DpoaClient.class);

    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)";

    private static final List<Header> CHECK_HEADERS = Collections.unmodifiableList(Lists.<Header>newArrayList(
            new BasicHeader("_eosajax", "xml"),
            new BasicHeader(HttpHeaders.ACCEPT, "*/*"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
            new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn"),
            new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"),
            new BasicHeader(HttpHeaders.CONNECTION, "Keep-Alive"),
            new BasicHeader("encoding", "utf-8"),
            new BasicHeader(HttpHeaders.HOST, "hr.deppon.com:9080"),
            new BasicHeader(HttpHeaders.REFERER, "http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.flow"),
            new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT),
            new BasicHeader("x-requsted-with", "XMLHttpRequest")
    ));

    private static final List<Header> LOGIN_HEADERS = Collections.unmodifiableList(Lists.<Header>newArrayList(
            new BasicHeader(HttpHeaders.ACCEPT, "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
            new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn"),
            new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"),
            new BasicHeader(HttpHeaders.CONNECTION, "Keep-Alive"),
            new BasicHeader(HttpHeaders.HOST, "ioa.deppon.com"),
            new BasicHeader(HttpHeaders.REFERER, "http://ioa.deppon.com/portal/login/index.action"),
            new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT)
    ));

    private static final List<Header> HOLIDAY_HEADERS = Collections.unmodifiableList(Lists.<Header>newArrayList(
            new BasicHeader(HttpHeaders.ACCEPT, "*/*"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
            new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn"),
            new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"),
            new BasicHeader(HttpHeaders.CONNECTION, "Keep-Alive"),
            new BasicHeader(HttpHeaders.HOST, "ioa.deppon.com"),
            new BasicHeader(HttpHeaders.REFERER, "http://ioa.deppon.com/portal/main/calendar_toShow.action"),
            new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT),
            new BasicHeader("x-requsted-with", "XMLHttpRequest")
    ));

    private static final List<Header> CLOCK_HEADERS = Collections.unmodifiableList(Lists.<Header>newArrayList(
            new BasicHeader(HttpHeaders.ACCEPT, "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
            new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN"),
            new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"),
            new BasicHeader(HttpHeaders.CONNECTION, "Keep-Alive"),
            new BasicHeader(HttpHeaders.HOST, "hr.deppon.com:9080"),
            new BasicHeader(HttpHeaders.REFERER, "http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.flow"),
            new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT)
    ));

    public static boolean check(String username, String password) {
        try {
            // 初始化用户名密码校验Cookies
            ResponseData responseData = HttpClientHelper.doGet("http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.flow");
            // 构建用户名密码校验URL地址
            String url = "http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.check.bizx.ajax";
            String paramTime = DateFormatUtils.format(new Date(), "EEE MMM d HH:mm:ss 'UTC'Z yyyy", Locale.US);
            url = HttpClientHelper.formatUrl(url, Collections.singletonMap("time", paramTime));
            // 构建用户名密码校验Cookies
            List<Cookie> cookies = responseData.getCookies();
            pushEosDefaultCookies(cookies, username);
            // 构建用户名密码校验参数
            password = Base64.encodeBase64String(DigestUtils.md5(password));
            password = URLEncoder.encode(password, Consts.UTF_8.name());
            String ajaxParam = String.format(
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><params><param><key>username</key><value>%s</value></param><param><key>jspassword</key><value>%s</value></param></params><data></data></root>",
                    username, password);
            Map<String, String> params = Collections.singletonMap("__ajaxParam", ajaxParam);
            // 执行用户名密码请求
            responseData = HttpClientHelper.doPost(url, CHECK_HEADERS, cookies, params);
            // 解析返回结果
            if (responseData.getStatusCode() == HttpStatus.SC_OK) {
                String contentStr = responseData.getContentStr();
                Document document = DocumentHelper.parseText(contentStr);
                Element root = document.getRootElement();
                Element data = root.element("data");
                Element returnValue = data.element("returnValue");
                String value = returnValue.getTextTrim();
                if (Integer.valueOf(value) == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}密码校验异常！", username, e);
        }
        return false;
    }

    public static List<Date> holidays(String username, String password, String monthStr) {
        try {
            // 定义登录URL地址
            String url = "http://ioa.deppon.com/portal/login/login_loginIn.action";
            // 初始化必要信息
            ResponseData responseData = HttpClientHelper.doPost(url,
                    Collections.singletonMap("username", StringUtils.EMPTY));
            // 构建登录参数
            Map<String, String> params = Maps.newHashMapWithExpectedSize(6);
            org.jsoup.nodes.Document document = Jsoup.parse(responseData.getContentStr());
            Elements elements = document.select("input[name=cookie][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("cookie", elements.first().val());
            }
            elements = document.select("input[name=casaction][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("casaction", elements.first().val());
            }
            elements = document.select("input[name=lt][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("ltname", elements.first().val());
            }
            elements = document.select("input[name=service][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("service", elements.first().val());
            }
            params.put("username", username);
            params.put("password", password);
            // 执行登录请求
            responseData = HttpClientHelper.doPost(url, LOGIN_HEADERS, responseData.getCookies(), params);
            // 定义获取假日URL地址
            url = "http://ioa.deppon.com/portal/main/calendar_getSelectCalendar.action";
            // 构建获取假日参数 (获取当前月请求字符串格式)
            params = Collections.singletonMap("vdate", monthStr);
            if (responseData.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                // 执行获取假日请求
                responseData = HttpClientHelper.doPost(url, HOLIDAY_HEADERS, responseData.getCookies(), params);
                // 解析假日信息
                String content = responseData.getContentStr();
                if (content != null && content.contains("#")) {
                    String[] holidays = content.split("#")[1].split(",");
                    List<Date> result = Lists.newArrayListWithCapacity(holidays.length);
                    for (String holiday : holidays) {
                        result.add(DateUtils.parseDate(holiday, "yyyy-M-d"));
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}获取假期异常！", username, e);
        }
        return Collections.emptyList();
    }


    public static boolean clock(String username, String password, boolean isClockIn) {
        try {
            // 定义打卡URL地址
            String url = "http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.flow";
            // 初始化必要信息
            ResponseData responseData = HttpClientHelper.doGet(url);
            // 构建打卡Cookies
            List<Cookie> cookies = responseData.getCookies();
            pushEosDefaultCookies(cookies, username);
            // 设置打卡参数
            Map<String, String> params = Maps.newHashMapWithExpectedSize(11);
            org.jsoup.nodes.Document document = Jsoup.parse(responseData.getContentStr());
            Elements elements = document.select("input[name=_eosFlowAction][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("_eosFlowAction", elements.first().val());
            }
            elements = document.select("input[name=_eosFlowDataContext][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("_eosFlowDataContext", elements.first().val());
            }
            elements = document.select("input[name=_eosFlowKey][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("_eosFlowKey", elements.first().val());
            }
            elements = document.select("input[name=ip][type=hidden]");
            if (!CollectionUtils.isEmpty(elements)) {
                params.put("ip", elements.first().val());
            }

            if (isClockIn) {
                params.put("worktime", "0");
            } else {
                params.put("worktime", "1");
            }

            params.put("empcode", username);
            params.put("moodmessage", StringUtils.EMPTY);
            params.put("moodscore", "5");
            params.put("offtype", "today");
            params.put("onclickcounts", "1");
            params.put("password", password);

            // 执行打卡
            responseData = HttpClientHelper.doPost(url, CLOCK_HEADERS, cookies, params);
            if (responseData.getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("{}{}打卡异常！", username, isClockIn ? "上班" : "下班", e);
        }
        return false;

    }

    private static void pushEosDefaultCookies(List<Cookie> cookies, String username) {
        for (BasicClientCookie cookie : new BasicClientCookie[]{
                new BasicClientCookie("flag", "ok"),
                new BasicClientCookie("workid", username)
        }) {
            cookie.setDomain(".hr.deppon.com");
            cookie.setPath("/eos-default/");
            cookie.setAttribute("domain", ".hr.deppon.com");
            cookie.setAttribute("path", "/eos-default/");
            cookies.add(cookie);
        }
    }

}
