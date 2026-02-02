package com.demo.authcenter.store;

import com.demo.authcenter.security.TokenStore;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenStore implements TokenStore {

    // jti -> expireAtMillis
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String jti, Instant expiresAt) {
        if (jti == null || jti.isBlank() || expiresAt == null) return;
        blacklist.put(jti, expiresAt.toEpochMilli());
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) return false;

        Long exp = blacklist.get(jti);
        if (exp == null) return false;

        if (exp < System.currentTimeMillis()) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }
}
