package com.demo.example.service;

import com.demo.authcenter.spi.AuthUser;
import com.demo.authcenter.spi.AuthUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 将业务用户接入 starter 的 SPI。
 * 约定：
 * - userId 是系统内唯一标识
 * - username 仅用于登录
 * - 鉴权阶段一律通过 userId（sub）
 */
@Service
public class DemoAuthUserService implements AuthUserService {

    @Override
    public AuthUser loadByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is blank");
        }

        // demo：用户名 -> userId 写死映射
        if ("admin".equals(username)) {
            return new AuthUser(
                    1L,
                    "admin",
                    List.of("api:add", "api:query", "ROLE_ADMIN")
            );
        }

        if ("user".equals(username)) {
            return new AuthUser(
                    2L,
                    "user",
                    List.of("api:query", "ROLE_USER")
            );
        }

        // 不存在用户：直接抛异常（推荐）
        throw new IllegalArgumentException("User not found: " + username);
    }

    @Override
    public AuthUser loadByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }

        // demo：userId -> 用户 写死映射
        if (userId == 1L) {
            return new AuthUser(
                    1L,
                    "admin",
                    List.of("api:add", "api:query", "ROLE_ADMIN")
            );
        }

        if (userId == 2L) {
            return new AuthUser(
                    2L,
                    "user",
                    List.of("api:query", "ROLE_USER")
            );
        }

        throw new IllegalArgumentException("User not found, userId=" + userId);
    }
}
