package com.demo.authcenter.security;

import com.demo.authcenter.spi.AuthUserService;
import io.jsonwebtoken.Claims;

import java.time.Instant;
import java.util.Objects;

public class RefreshService {

    private final JwtUtil jwtUtil;
    private final JwtProps jwtProps;
    private final TokenStore tokenStore;
    private final AuthUserService authUserService;

    public RefreshService(JwtUtil jwtUtil,
                          JwtProps jwtProps,
                          TokenStore tokenStore,
                          AuthUserService authUserService) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil);
        this.jwtProps = Objects.requireNonNull(jwtProps);
        this.tokenStore = Objects.requireNonNull(tokenStore);
        this.authUserService = Objects.requireNonNull(authUserService);
    }

    /**
     * refresh 最小可用 + 轮换：
     * 1) 校验 refresh token（签名/exp/iss + aud + typ=refresh）
     * 2) 检查 refresh jti 是否已被拉黑（已用/登出/踢下线）
     * 3) 立刻拉黑旧 refresh jti（rotation）
     * 4) 生成新的 access + refresh
     */
    public TokenPair rotate(String refreshToken) {
        if (!jwtProps.isRefreshEnabled()) {
            throw new IllegalStateException("Refresh is disabled (auth.jwt.refresh-enabled=false)");
        }

        Claims claims = jwtUtil.parseAndValidate(refreshToken);

        jwtUtil.validateAudience(claims);
        jwtUtil.validateRefreshType(claims);

        String oldJti = jwtUtil.getJti(claims);
        if (oldJti == null || oldJti.isBlank()) {
            throw new IllegalArgumentException("Missing jti");
        }
        if (tokenStore.isBlacklisted(oldJti)) {
            // 旧 refresh 已经用过（或登出）-> 拒绝
            throw new IllegalArgumentException("Refresh token already used/blacklisted");
        }

        Long userId = jwtUtil.getUserId(claims);
        if (userId == null) {
            throw new IllegalArgumentException("Missing sub(userId)");
        }

        // ✅ rotation：旧 refresh 立刻拉黑到它的 exp 为止
        Instant exp = claims.getExpiration().toInstant();
        tokenStore.blacklist(oldJti, exp);

        // 重新加载用户（保证权限最新）
        var user = authUserService.loadByUserId(userId);

        String newAccess = jwtUtil.generateAccessToken(user.userId(), user.username(), user.authorities());
        String newRefresh = jwtUtil.generateRefreshToken(user.userId());

        return new TokenPair(newAccess, newRefresh);
    }

    /**
     * 给 starter 用的轻量 TokenPair（避免依赖 demo 包）
     */
    public record TokenPair(String accessToken, String refreshToken) {}
}
