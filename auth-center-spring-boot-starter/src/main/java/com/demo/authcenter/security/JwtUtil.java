package com.demo.authcenter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * JWT 工具类（规范化：iss / aud / jti / typ(access|refresh)）
 *
 * 建议职责：
 * - 负责 token 的生成/解析/基础校验
 * - 业务相关的“黑名单/登出/刷新轮换”交给 TokenStore / Service 做
 */
public class JwtUtil {

    public static final String CLAIM_TYP = "typ";
    public static final String TYP_ACCESS = "access";
    public static final String TYP_REFRESH = "refresh";

    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLES = "roles";

    /** aud claim key（标准字段名） */
    public static final String CLAIM_AUD = "aud";

    private final JwtProps jwtProps;
    private final SecretKey key;

    public JwtUtil(JwtProps jwtProps) {
        this.jwtProps = Objects.requireNonNull(jwtProps, "jwtProps must not be null");
        this.key = initKey(jwtProps.getSecret());
        validateProps(jwtProps);
    }

    private void validateProps(JwtProps props) {
        if (!StringUtils.hasText(props.getIssuer())) {
            throw new IllegalArgumentException("auth.jwt.issuer must not be blank");
        }
        if (props.getAudience() == null || props.getAudience().isEmpty()) {
            throw new IllegalArgumentException("auth.jwt.audience must not be empty");
        }
        if (props.getAccessTtlSeconds() <= 0) {
            throw new IllegalArgumentException("auth.jwt.access-ttl-seconds must be > 0");
        }
        if (props.isRefreshEnabled() && props.getRefreshTtlSeconds() <= 0) {
            throw new IllegalArgumentException("auth.jwt.refresh-ttl-seconds must be > 0 when refresh enabled");
        }
        if (props.getClockSkewSeconds() < 0) {
            throw new IllegalArgumentException("auth.jwt.clock-skew-seconds must be >= 0");
        }
    }

    private SecretKey initKey(String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("auth.jwt.secret must not be blank");
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        // HS256 推荐至少 32 bytes；不足会导致运行期异常或安全性差
        if (bytes.length < 32) {
            throw new IllegalArgumentException("auth.jwt.secret length must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    // =========================
    // 生成 Token
    // =========================

    /**
     * 生成 Access Token（带 iss/aud/jti/typ=access，sub=userId）
     */
    public String generateAccessToken(Long userId, String username, Collection<String> roles) {
        return buildToken(userId, username, roles, TYP_ACCESS, jwtProps.getAccessTtlSeconds());
    }

    /**
     * 生成 Refresh Token（带 iss/aud/jti/typ=refresh，sub=userId）
     */
    public String generateRefreshToken(Long userId) {
        if (!jwtProps.isRefreshEnabled()) {
            throw new IllegalStateException("refresh is disabled by auth.jwt.refresh-enabled=false");
        }
        return buildToken(userId, null, null, TYP_REFRESH, jwtProps.getRefreshTtlSeconds());
    }

    private String buildToken(Long userId,
                              String username,
                              Collection<String> roles,
                              String typ,
                              long ttlSeconds) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("ttlSeconds must be > 0");
        }

        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        // ✅ 最稳：直接把 aud 写入 payload claim，避免不同 JJWT 版本 audience builder 行为差异
        List<String> audList = new ArrayList<>(jwtProps.getAudience());

        var builder = Jwts.builder()
                .issuer(jwtProps.getIssuer())                           // iss
                .subject(String.valueOf(userId))                        // sub=userId
                .id(jti)                                                // jti
                .issuedAt(Date.from(now))                               // iat
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))     // exp
                .claim(CLAIM_TYP, typ)                                  // typ
                .claim(CLAIM_AUD, audList)                              // aud (list)
                .signWith(key);

        // access 才放业务信息；refresh 尽量“瘦”
        if (StringUtils.hasText(username)) {
            builder.claim(CLAIM_USERNAME, username);
        }
        if (roles != null && !roles.isEmpty()) {
            builder.claim(CLAIM_ROLES, new ArrayList<>(roles));
        }

        return builder.compact();
    }

    // =========================
    // 解析 + 基础校验
    // =========================

    /**
     * 兼容你旧的 Filter 调用：jwtUtil.parse(token)
     * 内部直接走 parseAndValidate
     */
    public Claims parse(String token) {
        return parseAndValidate(token);
    }

    /**
     * 解析并校验签名、exp、iss（aud/typ 建议额外校验）
     */
    public Claims parseAndValidate(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token is blank");
        }
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(jwtProps.getIssuer())
                    .clockSkewSeconds(jwtProps.getClockSkewSeconds())
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT", e);
        }
    }

    /**
     * 校验 audience：token 的 aud 必须包含 allowed 任意一个
     */
    public void validateAudience(Claims claims) {
        List<String> tokenAud = extractAudience(claims);
        List<String> allowed = jwtProps.getAudience() == null ? List.of() : jwtProps.getAudience();

        boolean ok = tokenAud.stream().anyMatch(allowed::contains);
        if (!ok) {
            throw new IllegalArgumentException("Invalid audience");
        }
    }

    /**
     * 校验 typ=access
     */
    public void validateAccessType(Claims claims) {
        String typ = claims.get(CLAIM_TYP, String.class);
        if (!TYP_ACCESS.equals(typ)) {
            throw new IllegalArgumentException("Token type is not access");
        }
    }

    /**
     * 校验 typ=refresh
     */
    public void validateRefreshType(Claims claims) {
        String typ = claims.get(CLAIM_TYP, String.class);
        if (!TYP_REFRESH.equals(typ)) {
            throw new IllegalArgumentException("Token type is not refresh");
        }
    }

    /**
     * 取 jti（用于黑名单/登出）
     */
    public String getJti(Claims claims) {
        return claims.getId();
    }

    /**
     * 取 userId（sub）
     */
    public Long getUserId(Claims claims) {
        String sub = claims.getSubject();
        if (!StringUtils.hasText(sub)) return null;
        try {
            return Long.valueOf(sub);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 从 Header "Authorization: Bearer xxx" 提取 token
     */
    public String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) return null;
        String prefix = "Bearer ";
        if (authorizationHeader.startsWith(prefix)) {
            String t = authorizationHeader.substring(prefix.length()).trim();
            return t.isEmpty() ? null : t;
        }
        return null;
    }

    // =========================
    // internal helpers
    // =========================

    private List<String> extractAudience(Claims claims) {
        // 1) 先尝试标准 API（不同版本可能返回 String / Set / Collection）
        Object standardAudObj;
        try {
            standardAudObj = claims.getAudience();
        } catch (Exception e) {
            standardAudObj = null;
        }

        List<String> fromStandard = normalizeAudienceObject(standardAudObj);
        if (!fromStandard.isEmpty()) return fromStandard;

        // 2) 再从 claim map 取 aud（我们 buildToken 写入的是这个）
        Object aud = claims.get(CLAIM_AUD);
        return normalizeAudienceObject(aud);
    }

    private List<String> normalizeAudienceObject(Object audObj) {
        if (audObj == null) return List.of();

        if (audObj instanceof String s) {
            return StringUtils.hasText(s) ? List.of(s) : List.of();
        }
        if (audObj instanceof Collection<?> c) {
            if (c.isEmpty()) return List.of();
            List<String> list = new ArrayList<>();
            for (Object x : c) {
                if (x != null) {
                    String v = String.valueOf(x);
                    if (StringUtils.hasText(v)) list.add(v);
                }
            }
            return list;
        }
        // 兜底：转字符串
        String v = String.valueOf(audObj);
        return StringUtils.hasText(v) ? List.of(v) : List.of();
    }
}
