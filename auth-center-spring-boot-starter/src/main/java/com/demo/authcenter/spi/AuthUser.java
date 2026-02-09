package com.demo.authcenter.spi;

import java.util.List;

/**
 * 鉴权用户模型（由业务系统提供，Starter 仅消费）。
 *
 * <p>字段约定：
 * <ul>
 *   <li>userId：用户唯一标识（与 JWT 的 sub 一致）</li>
 *   <li>username：用户名（用于展示/日志/调试）</li>
 *   <li>authorities：权限字符串集合，既可包含角色也可包含权限点</li>
 * </ul>
 *
 * <p>authorities 约定示例：
 * <ul>
 *   <li>角色：以 {@code ROLE_} 前缀表示（如 {@code ROLE_ADMIN}）</li>
 *   <li>权限点：业务自定义字符串（如 {@code order:read}）</li>
 * </ul>
 *
 * <p>Starter 不区分“角色/权限点”的来源，统一交由 PermissionChecker 判断。</p>
 */
public record AuthUser(
        Long userId,
        String username,
        List<String> authorities
) {
    public AuthUser {
        authorities = authorities == null ? List.of() : List.copyOf(authorities);
    }
}
