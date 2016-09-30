package com.accelerator.framework.spring.boot.autoconfigure.httpclient;

import com.accelerator.framework.httpclient.HttpClientFactoryBean;
import com.accelerator.framework.httpclient.HttpClientHelper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({CloseableHttpClient.class})
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(name = HttpClientHelper.HTTP_CLIENT_BEAN_NAME)
    @ConditionalOnMissingBean(name = HttpClientHelper.HTTP_CLIENT_BEAN_NAME)
    public HttpClientFactoryBean httpClient(HttpClientProperties httpClientProperties) {
        HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
        BeanUtils.copyProperties(httpClientProperties, httpClientFactoryBean);
        return httpClientFactoryBean;
    }

}
