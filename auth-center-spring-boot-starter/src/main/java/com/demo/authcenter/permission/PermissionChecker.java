package com.demo.authcenter.permission;

import org.springframework.security.core.Authentication;

public interface PermissionChecker {
    boolean hasPerm(Authentication authentication, String perm);
}
