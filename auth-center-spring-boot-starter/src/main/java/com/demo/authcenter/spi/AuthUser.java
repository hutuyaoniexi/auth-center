package com.demo.authcenter.spi;

import java.util.List;

/**
 * 鉴权用户模型（由业务系统提供）
 *
 * starter 只关心：
 * 1. 用户名
 * 2. 拥有哪些权限 / 角色（authorities）
 */
public record AuthUser(
        Long userId,
        String username,
        List<String> authorities
) {
}
