package com.demo.authcenter.security;

import com.demo.authcenter.properties.JwtProps;
import com.demo.authcenter.security.dto.JwtTokenPair;
import com.demo.authcenter.spi.AuthUserService;
import com.demo.authcenter.store.TokenStore;
import io.jsonwebtoken.Claims;

import java.time.Instant;
import java.util.Objects;

/**
 * Refresh 服务：校验 refresh token 并执行 rotation（旧 refresh 立即作废），返回新的 token 对。
 */
public class RefreshService {

    private final JwtUtil jwtUtil;
    private final JwtProps jwtProps;
    private final TokenStore tokenStore;
    private final AuthUserService authUserService;

    public RefreshService(JwtUtil jwtUtil,
                          JwtProps jwtProps,
                          TokenStore tokenStore,
                          AuthUserService authUserService) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil, "jwtUtil must not be null");
        this.jwtProps = Objects.requireNonNull(jwtProps, "jwtProps must not be null");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore must not be null");
        this.authUserService = Objects.requireNonNull(authUserService, "authUserService must not be null");
    }

    /**
     * 执行 refresh rotation：
     * 1) 校验 refresh（签名/exp/iss + aud + typ=refresh）
     * 2) rotation（原子）：旧 refresh 的 jti 只能成功作废一次；失败则视为已用/已登出
     * 3) 重新加载用户信息并签发新的 access + refresh
     */
    public JwtTokenPair rotate(String refreshToken) {
        if (!jwtProps.isRefreshEnabled()) {
            throw new IllegalStateException("Refresh is disabled (auth.jwt.refresh-enabled=false)");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken is blank");
        }

        Claims claims = jwtUtil.parseAndValidate(refreshToken);
        jwtUtil.validateAudience(claims);
        jwtUtil.validateRefreshType(claims);

        String oldJti = jwtUtil.getJti(claims);
        if (oldJti == null || oldJti.isBlank()) {
            throw new IllegalArgumentException("Missing jti");
        }

        Long userId = jwtUtil.getUserId(claims);
        if (userId == null) {
            throw new IllegalArgumentException("Missing sub(userId)");
        }

        var expDate = claims.getExpiration();
        if (expDate == null) {
            throw new IllegalArgumentException("Missing exp");
        }
        Instant exp = expDate.toInstant();

        // ✅ 原子 rotation：只能成功一次（防并发重放）
        // - 返回 false：说明该 jti 已经被作废（已用/已登出/被踢下线）
        // - exp 已过期：无需入黑名单，但此时 refresh 本身也应视为无效
        if (exp.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        boolean rotated = tokenStore.blacklistIfAbsent(oldJti, exp);
        if (!rotated) {
            throw new IllegalArgumentException("Refresh token already used/blacklisted");
        }

        var user = authUserService.loadByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        String newAccess = jwtUtil.generateAccessToken(user.userId(), user.username(), user.authorities());
        String newRefresh = jwtUtil.generateRefreshToken(user.userId());

        return new JwtTokenPair(newAccess, newRefresh);
    }
}
