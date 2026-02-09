package com.demo.authcenter.security;

import com.demo.authcenter.store.TokenStore;
import io.jsonwebtoken.Claims;

import java.time.Instant;
import java.util.Objects;

/**
 * 登出服务：将 token 的 jti 加入黑名单，使其在到期前不可用。
 *
 * <p>说明：JWT 无状态，登出/踢下线需配合 TokenStore 记录失效状态。</p>
 */
public class LogoutService {

    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public LogoutService(JwtUtil jwtUtil, TokenStore tokenStore) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil, "jwtUtil must not be null");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore must not be null");
    }

    /**
     * 仅作废 access token（兼容旧用法）。
     */
    public void logout(String accessToken) {
        blacklistToken(accessToken, JwtUtil.TYP_ACCESS);
    }

    /**
     * 同时作废 access + refresh。
     *
     * @param accessToken  access token（Bearer 已剥离）
     * @param refreshToken refresh token（Bearer 已剥离，可为空；refresh 未启用时会被忽略）
     */
    public void logout(String accessToken, String refreshToken) {
        blacklistToken(accessToken, JwtUtil.TYP_ACCESS);

        // refresh 可能未开启：此时忽略 refreshToken
        try {
            // 这里不直接依赖 JwtProps，避免 LogoutService 引入更多配置对象；
            // refresh 是否启用由 jwtUtil.generateRefreshToken 的配置决定，但登出时只要传了就尝试作废。
            if (refreshToken != null && !refreshToken.isBlank()) {
                blacklistToken(refreshToken, JwtUtil.TYP_REFRESH);
            }
        } catch (IllegalStateException e) {
            // refresh 体系关闭时，业务可能仍传了 refreshToken；选择忽略更“宽容”
        }
    }

    /**
     * 解析 token -> 校验 typ -> jti 入黑名单到 exp。
     */
    private void blacklistToken(String token, String expectedTyp) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(expectedTyp + " token is blank");
        }

        Claims claims = jwtUtil.parseAndValidate(token);
        if (JwtUtil.TYP_ACCESS.equals(expectedTyp)) {
            jwtUtil.validateAccessType(claims);
        } else if (JwtUtil.TYP_REFRESH.equals(expectedTyp)) {
            jwtUtil.validateRefreshType(claims);
        } else {
            throw new IllegalArgumentException("Unknown token type: " + expectedTyp);
        }

        String jti = jwtUtil.getJti(claims);
        if (jti == null || jti.isBlank()) {
            throw new IllegalArgumentException("Missing jti");
        }

        var expDate = claims.getExpiration();
        if (expDate == null) {
            throw new IllegalArgumentException("Missing exp");
        }

        Instant exp = expDate.toInstant();
        if (exp.isBefore(Instant.now())) {
            return; // 已过期，无需拉黑
        }

        tokenStore.blacklist(jti, exp);
    }
}
