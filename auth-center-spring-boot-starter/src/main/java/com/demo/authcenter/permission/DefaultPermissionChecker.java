package com.demo.authcenter.permission;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class DefaultPermissionChecker implements PermissionChecker {

    @Override
    public boolean hasPerm(Authentication authentication, String perm) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            if (perm.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}
