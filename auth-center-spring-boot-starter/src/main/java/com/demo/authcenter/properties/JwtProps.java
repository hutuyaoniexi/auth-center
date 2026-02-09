package com.demo.authcenter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * JWT 配置属性（auth-center.jwt.*）：
 *
 * <p>用于配置 JWT 的签名密钥、发行方、受众以及 access/refresh 的过期策略。</p>
 *
 * <p>说明：
 * <ul>
 *   <li>Token 解析协议固定为：Authorization: Bearer &lt;token&gt;（不在此配置）</li>
 *   <li>本类仅承载 JWT 生成与校验所需参数，参数合法性由 JwtUtil 统一校验</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "auth-center.jwt")
public class JwtProps {

    /**
     * JWT 签名密钥（HS256）。
     *
     * <p>要求：UTF-8 编码后长度 ≥ 32 bytes。</p>
     * <p>建议通过环境变量/配置中心注入，避免明文提交到仓库。</p>
     */
    private String secret;

    /**
     * JWT issuer（iss）。
     */
    private String issuer = "auth-center";

    /**
     * JWT audience（aud）。
     *
     * <p>校验时要求 Token 的 aud 至少包含其中任意一个值。</p>
     * <p>建议配置为业务系统的唯一标识（如应用名）。</p>
     */
    private List<String> audience = List.of("demo-app");

    /**
     * Access Token 有效期（秒）。
     */
    private long accessTtlSeconds = 1800;

    /**
     * 是否启用 Refresh Token。
     */
    private boolean refreshEnabled = false;

    /**
     * Refresh Token 有效期（秒）。仅在 refreshEnabled=true 时生效。
     */
    private long refreshTtlSeconds = 604800;

    /**
     * 时钟偏移容忍（秒）：用于校验 exp/nbf 等时间类字段的容错。
     */
    private long clockSkewSeconds = 30;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public void setAccessTtlSeconds(long accessTtlSeconds) {
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }

    public void setRefreshTtlSeconds(long refreshTtlSeconds) {
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public long getClockSkewSeconds() {
        return clockSkewSeconds;
    }

    public void setClockSkewSeconds(long clockSkewSeconds) {
        this.clockSkewSeconds = clockSkewSeconds;
    }
}
