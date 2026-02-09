package com.demo.authcenter.store;

import java.time.Instant;

/**
 * TokenStore：用于管理 JWT 的“失效状态”，弥补 JWT 天然无状态的不足。
 *
 * <p>主要解决两类问题：
 * <ul>
 *   <li>主动注销 / 踢下线：将 access token 标记为失效</li>
 *   <li>Refresh rotation：旧 refresh token 必须立即作废，防止重放</li>
 * </ul>
 *
 * <p>设计约定：
 * <ul>
 *   <li>以 jti（JWT ID）作为唯一键</li>
 *   <li>失效状态只需保存到 token 自身的过期时间（expiresAt）</li>
 * </ul>
 *
 * <p>默认实现 {@code InMemoryTokenStore} 仅用于 demo/本地测试；
 * 生产环境建议业务方提供 Redis 等持久化实现并覆盖 Bean。</p>
 */
public interface TokenStore {

    /**
     * 将指定 jti 拉黑到 expiresAt 为止（幂等）。
     *
     * <p>适用于：
     * <ul>
     *   <li>access token 登出</li>
     *   <li>refresh token rotation 后作废旧 token</li>
     * </ul>
     */
    void blacklist(String jti, Instant expiresAt);

    /**
     * 原子拉黑：仅当 jti 尚未被拉黑时才执行。
     *
     * <p>返回值语义：
     * <ul>
     *   <li>true：成功拉黑（首次使用）</li>
     *   <li>false：该 jti 已被拉黑（已用 / 已登出 / 被踢下线）</li>
     * </ul>
     *
     * <p>用于 refresh token 的 rotation，防止并发重放。</p>
     */
    boolean blacklistIfAbsent(String jti, Instant expiresAt);

    /**
     * 判断指定 jti 是否已被拉黑。
     *
     * <p>实现方应自行处理过期数据的清理（如 expiresAt 已过期）。</p>
     */
    boolean isBlacklisted(String jti);
}
