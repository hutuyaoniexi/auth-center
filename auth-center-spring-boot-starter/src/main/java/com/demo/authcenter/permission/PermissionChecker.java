package com.demo.authcenter.permission;

import org.springframework.security.core.Authentication;

/**
 * 权限校验 SPI：定义“当前用户是否拥有指定权限”的判断契约。
 *
 * <p>调用时机：由 @RequirePerm 的 AOP 拦截器在方法调用前触发。</p>
 *
 * <p>参数约定：
 * <ul>
 *   <li>authentication：当前请求对应的 Spring Security Authentication</li>
 *   <li>perm：权限字符串（如 "order:read"），通常来源于 @RequirePerm(perms)</li>
 * </ul>
 *
 * <p>Starter 默认实现 {@link DefaultPermissionChecker}：
 * 仅判断 authentication.authorities 是否包含该权限字符串。</p>
 *
 * <p>业务系统可通过自定义实现 PermissionChecker Bean，
 * 覆盖默认逻辑（如角色映射、通配符、ABAC、远程权限校验等）。</p>
 */
public interface PermissionChecker {

    /**
     * 判断当前用户是否拥有指定权限。
     *
     * @param authentication 当前用户身份信息
     * @param perm 权限字符串（如 "order:read"）
     * @return 是否拥有该权限
     */
    boolean hasPerm(Authentication authentication, String perm);
}
