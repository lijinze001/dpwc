package com.accelerator.framework.spring.boot.autoconfigure.web.security;

import com.accelerator.framework.spring.security.AbstractCustomAuthenticationManager;
import com.accelerator.framework.spring.security.CustomAuthenticationManager;
import com.accelerator.framework.spring.security.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@ConditionalOnClass({WebSecurityConfigurerAdapter.class, AuthenticationManager.class})
@AutoConfigureAfter({SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class WebSecurityAutoConfiguration {

    @Configuration
    public static class CustomWebSecurityConfiguration {

        @Bean @ConditionalOnMissingBean(CustomWebSecurityConfigurerAdapter.class)
        public CustomWebSecurityConfigurerAdapter customWebSecurityConfigurerAdapter() {
            return new CustomWebSecurityConfigurerAdapter();
        }

        @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
        protected static class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

            @Autowired
            private CustomAuthenticationManager customAuthenticationManager;

            @Autowired
            private SecurityProperties security;

            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                SecurityProperties.User admin = security.getUser();
                // 创建DpwcAuthenticationProvider
                CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
                authenticationProvider.setAdmin(new User(admin.getName(), admin.getPassword(),
                        customAuthenticationManager.getGrantedAuthorities(admin.getRole())));
                authenticationProvider.setCustomAuthenticationManager(customAuthenticationManager);
                authenticationProvider.afterPropertiesSet();
                // 设置AuthenticationProvider
                auth.authenticationProvider(authenticationProvider);
            }

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                List<String> adminRoles = security.getUser().getRole();
                String realm = security.getBasic().getRealm();
                String defaultRole = AbstractCustomAuthenticationManager.DEFAULT_ROLE_NAME;
                // 所有地址均需要访问均需验证权限
                int adminRoleSize = adminRoles.size();
                String[] roles = new String[adminRoleSize + 1];
                System.arraycopy(adminRoles.toArray(), 0, roles, 1, adminRoleSize);
                roles[0] = defaultRole;
                http.authorizeRequests().anyRequest().hasAnyRole(roles);
                http.httpBasic().realmName(realm);
            }

        }
    }

    @Configuration @ConditionalOnClass(H2ConsoleAutoConfiguration.class)
    @AutoConfigureAfter(H2ConsoleAutoConfiguration.class) @EnableConfigurationProperties(H2ConsoleProperties.class)
    @ConditionalOnProperty(prefix = "security.basic", name = "enabled", havingValue = "false")
    public static class H2ConsoleSecurityConfiguration {

        @Bean @ConditionalOnMissingBean(H2ConsoleWebSecurityConfigurerAdapter.class)
        public H2ConsoleWebSecurityConfigurerAdapter h2ConsoleWebSecurityConfigurerAdapter() {
            return new H2ConsoleWebSecurityConfigurerAdapter();
        }

        @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
        protected static class H2ConsoleWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

            @Autowired
            private SecurityProperties security;

            @Autowired
            private ManagementServerProperties management;

            @Autowired
            private H2ConsoleProperties h2Console;

            private void configurePermittedRequests(
                    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry requests) {
                List<String> roles = management.getSecurity().getRoles();
                requests.anyRequest().hasAnyRole(roles.toArray(new String[roles.size()]));
            }

            private RequestMatcher getRequestMatcher(String path) {
                String pattern = path.endsWith("/") ? path + "**" : path + "/**";
                return new AntPathRequestMatcher(pattern);
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                if (this.security.isRequireSsl()) {
                    http.requiresChannel().anyRequest().requiresSecure();
                }
                http.requestMatcher(getRequestMatcher(h2Console.getPath()));
                http.sessionManagement().sessionCreationPolicy(management.getSecurity().getSessions());
                http.httpBasic().realmName(security.getBasic().getRealm());
                http.csrf().disable();
                http.headers().frameOptions().sameOrigin();
                configurePermittedRequests(http.authorizeRequests());
            }

        }

    }

}
