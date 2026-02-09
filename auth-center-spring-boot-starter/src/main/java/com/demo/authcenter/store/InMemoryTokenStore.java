package com.demo.authcenter.store;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemory TokenStore：仅用于 demo/本地测试。
 *
 * <p>实现方式：使用 ConcurrentHashMap 记录 jti -> expiresAtMillis，
 * 到期后在查询时惰性清理。</p>
 */
public class InMemoryTokenStore implements TokenStore {

    /** jti -> expiresAtMillis */
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String jti, Instant expiresAt) {
        long expMillis = toExpMillisOrNeg(jti, expiresAt);
        if (expMillis < 0) return;

        // 幂等覆盖：同一个 jti 反复拉黑无副作用
        blacklist.put(jti, expMillis);
    }

    @Override
    public boolean blacklistIfAbsent(String jti, Instant expiresAt) {
        long expMillis = toExpMillisOrNeg(jti, expiresAt);
        if (expMillis < 0) return false;

        // putIfAbsent 原子：并发下只会有一个请求成功
        Long prev = blacklist.putIfAbsent(jti, expMillis);
        if (prev == null) return true;

        // 已存在：若已过期则尝试替换为新的 exp（可选兜底，避免“旧过期值卡住”）
        if (prev < System.currentTimeMillis()) {
            boolean replaced = blacklist.replace(jti, prev, expMillis);
            return replaced; // 替换成功视为“本次成功作废”
        }
        return false;
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) return false;

        Long exp = blacklist.get(jti);
        if (exp == null) return false;

        long now = System.currentTimeMillis();
        if (exp <= now) {
            // 惰性清理：并发下用 remove(key,value) 避免误删新值
            blacklist.remove(jti, exp);
            return false;
        }
        return true;
    }

    private static long toExpMillisOrNeg(String jti, Instant expiresAt) {
        if (jti == null || jti.isBlank() || expiresAt == null) return -1;
        long expMillis = expiresAt.toEpochMilli();
        // 已过期的不必存
        if (expMillis <= System.currentTimeMillis()) return -1;
        return expMillis;
    }
}
