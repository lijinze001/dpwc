package com.accelerator.dpwc;

import com.accelerator.framework.httpclient.HttpClientHelper;
import com.accelerator.framework.httpclient.ResponseData;
import com.accelerator.framework.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class DpoaClient {

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

    private static final List<Header> INDEX_HEADERS = Collections.unmodifiableList(Lists.<Header>newArrayList(
            new BasicHeader(HttpHeaders.ACCEPT, "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*"),
            new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
            new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN"),
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

    public static boolean check(String username, String password) throws Exception {
        // 初始化用户名密码校验Cookies
        ResponseData responseData = HttpClientHelper.doGet("http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.flow");
        // 构建用户名密码校验URL地址
        String url = "http://hr.deppon.com:9080/eos-default/dip.integrateorg.oaAttence.oaAttence.check.bizx.ajax";
        String paramTime = DateFormatUtils.format(DateUtils.createNow(), "EEE MMM d HH:mm:ss 'UTC'Z yyyy", Locale.US);
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
        return false;
    }

    public static String nickname(List<Cookie> cookies) throws Exception {
        if (!CollectionUtils.isEmpty(cookies)) {
            // 定义首页URL地址
            String url = "http://ioa.deppon.com/portal/main/index.action";
            // 执行获取首页请求
            ResponseData responseData = HttpClientHelper
                    .doGet(url, INDEX_HEADERS, cookies, Collections.<String, String>emptyMap());
            if (responseData.getStatusCode() == HttpStatus.SC_OK) {
                // 解析首页信息
                org.jsoup.nodes.Document document = Jsoup.parse(responseData.getContentStr());
                org.jsoup.nodes.Element element = document.getElementById("empLink");
                return element.text();
            }
        }
        return StringUtils.EMPTY;
    }

    public static List<Date> holidays(List<Cookie> cookies, String monthStr) throws Exception {
        // 定义获取假日URL地址
        String url = "http://ioa.deppon.com/portal/main/calendar_getSelectCalendar.action";
        // 构建获取假日参数 (获取当前月请求字符串格式)
        Map<String, String> params = Collections.singletonMap("vdate", monthStr);
        if (!CollectionUtils.isEmpty(cookies)) {
            // 执行获取假日请求
            ResponseData responseData = HttpClientHelper.doPost(url, HOLIDAY_HEADERS, cookies, params);
            // 解析假日信息
            String content = responseData.getContentStr();
            if (content != null && content.contains("#")) {
                String[] calendarInfo = content.split("#");
                if (2 == calendarInfo.length) {
                    String holidayStr = calendarInfo[1];
                    String[] holidays = holidayStr.split(",");
                    List<Date> result = Lists.newArrayListWithCapacity(holidays.length);
                    for (String holiday : holidays) {
                        result.add(DateUtils.parseDate(holiday, "yyyy-M-d"));
                    }
                    return result;
                }
            }
        }
        return Collections.emptyList();
    }

    public static boolean clock(String username, String password, boolean isClockIn) throws Exception {
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
        return responseData.getStatusCode() == HttpStatus.SC_OK;
    }

    public static List<Cookie> login(String username, String password) {
        // 定义登录URL地址
        String url = "http://ioa.deppon.com/portal/login/login_loginIn.action";
        // 构建登录参数
        Map<String, String> params = Maps.newHashMapWithExpectedSize(2);
        params.put("username", username);
        params.put("password", password);
        // 执行登录请求
        ResponseData responseData = HttpClientHelper.doPost(url, LOGIN_HEADERS, Collections.emptyList(), params);
        if (responseData.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
            return responseData.getCookies();
        }
        return Collections.emptyList();
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
