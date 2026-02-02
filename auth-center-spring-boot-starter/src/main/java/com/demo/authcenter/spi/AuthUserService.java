package com.demo.authcenter.spi;

import java.util.Objects;

/**
 * 业务侧用户加载 SPI（Starter 只定义接口，不关心你是 DB/LDAP/内存/第三方）
 *
 * 规范约定：
 * - Access/Refresh Token 的 sub = userId（Long）
 * - Filter 鉴权阶段通过 userId 加载用户、权限
 * - 登录阶段通常通过 username 加载用户（校验密码、获取 userId/权限后签发 token）
 */
public interface AuthUserService {

    /**
     * 登录/签发 Token 阶段常用：通过用户名加载用户信息
     *
     * @param username 用户名（业务定义：可为账号/邮箱/手机号等）
     * @return 用户信息（包含 userId、username、authorities 等）
     */
    AuthUser loadByUsername(String username);

    /**
     * 鉴权阶段必须：通过 userId 加载用户信息（规范版：sub=userId）
     *
     * @param userId 用户唯一ID
     * @return 用户信息（包含 userId、username、authorities 等）
     */
    AuthUser loadByUserId(Long userId);

    /**
     * 可选：用户是否存在（用于提前校验/减少异常）
     */
    default boolean existsByUserId(Long userId) {
        try {
            return loadByUserId(Objects.requireNonNull(userId)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 可选：用户名是否存在（用于提前校验/减少异常）
     */
    default boolean existsByUsername(String username) {
        try {
            return loadByUsername(Objects.requireNonNull(username)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}

