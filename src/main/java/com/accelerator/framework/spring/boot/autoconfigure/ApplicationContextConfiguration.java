package com.accelerator.framework.spring.boot.autoconfigure;

import com.accelerator.framework.message.MessageProvider;
import com.accelerator.framework.message.NLS;
import com.accelerator.framework.message.SpringMessageProvider;
import com.accelerator.framework.spring.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.mvc.LogFileMvcEndpoint;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.nio.charset.Charset;


@Configuration
public class ApplicationContextConfiguration {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieMaxAge(3600);
        return localeResolver;
    }

    @Bean(name = NLS.MESSAGE_PROVIDER_BEAN_NAME)
    public MessageProvider messageProvider(MessageSource messageSource) {
        SpringMessageProvider messageProvider = new SpringMessageProvider();
        messageProvider.setMessageSource(messageSource);
        return messageProvider;
    }

    @Bean(name = ConfigurationPropertiesBindingPostProcessor.VALIDATOR_BEAN_NAME)
    public LocalValidatorFactoryBean configurationPropertiesValidator(MessageSource messageSource,
            ApplicationContext applicationContext) {
        LocalValidatorFactoryBean configurationPropertiesValidator = new LocalValidatorFactoryBean();
        configurationPropertiesValidator.setValidationMessageSource(messageSource);
        configurationPropertiesValidator.setApplicationContext(applicationContext);
        return configurationPropertiesValidator;
    }

    @Bean @SuppressWarnings("SpringJavaAutowiringInspection")
    public FilterRegistrationBean logfileContentTypeFilter(LogFileMvcEndpoint logFileMvcEndpoint) {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        String path = logFileMvcEndpoint.getPath();
        filterRegistration.addUrlPatterns(path);
        if (logFileMvcEndpoint.isSensitive()) {
            filterRegistration.addUrlPatterns(path + "/**");
            filterRegistration.addUrlPatterns(path + ".*");
        }
        filterRegistration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                response = new HttpServletResponseWrapper(response) {
                    @Override
                    public void setContentType(String type) {
                        MediaType mediaType = MediaType.parseMediaType(type);
                        if (mediaType.getCharset() == null) {
                            mediaType = new MediaType(mediaType.getType(), mediaType.getSubtype(),
                                    Charset.defaultCharset());
                        }
                        super.setContentType(mediaType.toString());
                    }
                };
                filterChain.doFilter(request, response);
            }
        });
        return filterRegistration;
    }
}
