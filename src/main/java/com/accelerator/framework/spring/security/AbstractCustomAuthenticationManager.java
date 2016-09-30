package com.accelerator.framework.spring.security;

import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public abstract class AbstractCustomAuthenticationManager implements CustomAuthenticationManager {

    public static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    public static final String DEFAULT_ROLE_NAME = "USER";

    private String rolePrefix = DEFAULT_ROLE_PREFIX;

    @Override
    public GrantedAuthority getGrantedAuthority(String roleName) {
        String role = getRolePrefix().concat(roleName);
        return new SimpleGrantedAuthority(role);
    }

    @Override
    public List<GrantedAuthority> getGrantedAuthorities(List<String> roleNames) {
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayListWithCapacity(roleNames.size());
        for (String roleName : roleNames) {
            grantedAuthorities.add(getGrantedAuthority(roleName));
        }
        return grantedAuthorities;
    }

    public String getRolePrefix() {
        return rolePrefix;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

}
