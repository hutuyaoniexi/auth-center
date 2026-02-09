package com.demo.authcenter.spi;

/**
 * AuthUserService（SPI）：业务系统提供用户加载能力，Starter 只依赖此接口。
 *
 * 约定：
 * - JWT 的 sub = userId（Long），鉴权阶段通过 userId 加载用户与权限
 * - 登录阶段通常通过 username 加载用户（校验密码后签发 token）
 *
 * 返回的 AuthUser 建议包含 authorities（角色/权限字符串），例如：
 * - "ROLE_ADMIN"
 * - "order:read"
 */
public interface AuthUserService {

    /**
     * 登录/签发 token：根据用户名加载用户信息（业务自定义 username 含义：账号/邮箱/手机号等）。
     */
    AuthUser loadByUsername(String username);

    /**
     * 鉴权阶段：根据 userId 加载用户信息（来自 token 的 sub）。
     */
    AuthUser loadByUserId(Long userId);
}
