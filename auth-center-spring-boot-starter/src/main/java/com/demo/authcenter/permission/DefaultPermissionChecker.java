package com.demo.authcenter.permission;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

/**
 * 默认权限判定：当用户的 authorities 中包含目标权限字符串时返回 true。
 *
 * <p>约定：权限以字符串表示（如 "order:read"），通常由 JwtAuthFilter 写入 Authentication。</p>
 * <p>业务可替换 PermissionChecker 以支持角色映射、通配符、ABAC 等策略。</p>
 */
public class DefaultPermissionChecker implements PermissionChecker {

    @Override
    public boolean hasPerm(Authentication authentication, String perm) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        if (perm == null || perm.isBlank()) return false;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) return false;

        for (GrantedAuthority ga : authorities) {
            if (ga != null && Objects.equals(perm, ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
