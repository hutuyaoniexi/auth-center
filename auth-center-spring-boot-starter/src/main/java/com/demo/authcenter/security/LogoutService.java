package com.demo.authcenter.security;

import io.jsonwebtoken.Claims;

import java.time.Instant;
import java.util.Objects;

public class LogoutService {

    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public LogoutService(JwtUtil jwtUtil, TokenStore tokenStore) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil);
        this.tokenStore = Objects.requireNonNull(tokenStore);
    }

    /**
     * 登出：将 access token 的 jti 拉黑到原 token 过期为止
     */
    public void logout(String accessToken) {
        Claims claims = jwtUtil.parseAndValidate(accessToken);

        // 防止拿 refresh 来登出（可选，但推荐）
        jwtUtil.validateAccessType(claims);

        String jti = jwtUtil.getJti(claims);
        Instant exp = claims.getExpiration().toInstant();

        tokenStore.blacklist(jti, exp);
    }
}
