package com.accelerator.framework.spring.security;

import com.accelerator.framework.exception.CustomException;
import com.accelerator.framework.util.ExceptionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

public class CustomAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private CustomAuthenticationManager customAuthenticationManager;

    private User admin;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(customAuthenticationManager, "customAuthenticationManager is mandatory");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        Object credentials = authentication.getCredentials();
        String password = credentials == null ? null : credentials.toString();
        if (admin != null && admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(username, password, admin.getAuthorities());
        }
        try {
            return customAuthenticationManager.attemptAuthentication(username, password);
        } catch (CustomException e) {
            Throwable cause = ExceptionUtils.getRootCause(e);
            if (cause == null) {
                cause = e;
            }
            throw new AuthenticationException(e.getMessage(), cause) {
                private static final long serialVersionUID = -7248697935597391738L;
            };
        }
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setCustomAuthenticationManager(CustomAuthenticationManager customAuthenticationManager) {
        this.customAuthenticationManager = customAuthenticationManager;
    }
}
