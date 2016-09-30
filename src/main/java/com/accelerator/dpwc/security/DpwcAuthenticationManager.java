package com.accelerator.dpwc.security;

import com.accelerator.dpwc.DpoaClient;
import com.accelerator.dpwc.domain.User;
import com.accelerator.dpwc.domain.UserRepository;
import com.accelerator.dpwc.exception.AuthException;
import com.accelerator.dpwc.exception.ErrorCode;
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
import java.util.Date;
import java.util.List;

@Component("customAuthenticationManager")
public class DpwcAuthenticationManager extends AbstractCustomAuthenticationManager {

    @Resource
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties security;

    @Override
    public Authentication attemptAuthentication(String username, String password) throws CustomException {
        if (DpoaClient.check(username, password)) {
            List<GrantedAuthority> grantedAuthorities = Collections.singletonList(
                    getGrantedAuthority(DEFAULT_ROLE_NAME));
            if (!security.getUser().getName().equals(username)) {
                Date now = new Date();
                User user = userRepository.findOne(username);
                if (user == null) {
                    user = new User(username);
                    user.setCreateUser(username);
                    user.setCreateTime(now);
                }
                user.setUpdateUser(username);
                user.setUpdateTime(now);
                user.setPassword(password);
                userRepository.save(user);
            }
            return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
        }
        throw new AuthException(ErrorCode.WRONG_USER_OR_PASSWORD);
    }

}
