package com.accelerator.framework.spring.security;

import com.accelerator.framework.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface CustomAuthenticationManager {

    Authentication attemptAuthentication(String username, String password) throws CustomException;

    GrantedAuthority getGrantedAuthority(String roleName);

    List<GrantedAuthority> getGrantedAuthorities(List<String> roleNames);

}
