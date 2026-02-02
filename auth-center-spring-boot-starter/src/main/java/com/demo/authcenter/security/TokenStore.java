package com.demo.authcenter.security;

import java.time.Instant;

public interface TokenStore {

    /**
     * 拉黑某个 token（jti），直到 expiresAt
     */
    void blacklist(String jti, Instant expiresAt);

    /**
     * 是否已拉黑
     */
    boolean isBlacklisted(String jti);
}
