package com.accelerator.dpwc.security;

import com.accelerator.dpwc.exception.AuthException;
import com.accelerator.dpwc.exception.ErrorCode;
import com.accelerator.dpwc.service.DpwcService;
import com.accelerator.framework.exception.CustomException;
import com.accelerator.framework.spring.security.AbstractCustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component("customAuthenticationManager")
public class DpwcAuthenticationManager extends AbstractCustomAuthenticationManager {

    @Autowired
    private SecurityProperties security;

    @Resource
    private DpwcService dpwcService;

    @Override
    public Authentication attemptAuthentication(String username, String password) throws CustomException {
        try {
           // if (DpoaClient.check(username, password)) {
                List<GrantedAuthority> grantedAuthorities =
                        Collections.singletonList(getGrantedAuthority(DEFAULT_ROLE_NAME));
                if (!security.getUser().getName().equals(username)) {
                    dpwcService.addUser(username, password);
                }
                return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
           // }
        } catch (Exception e) {
            logger.error("{}密码校验异常！", username, e);
        }
        throw new AuthException(ErrorCode.WRONG_USER_OR_PASSWORD);
    }

}
