/**
 * Token 状态存储（TokenStore）。
 *
 * <p>本包用于解决 JWT 无状态带来的“主动失效”需求，通过 jti 追踪 token 状态。</p>
 *
 * <p>典型能力：</p>
 * <ul>
 *   <li>登出/踢人：将 jti 拉黑或标记失效</li>
 *   <li>刷新 rotate：旧 token 链路作废，防止重放</li>
 *   <li>可替换实现：InMemory（默认）/ Redis / JDBC 等</li>
 * </ul>
 *
 * <p>默认实现 InMemoryTokenStore 仅用于本地与 demo，生产建议替换为 Redis/JDBC。</p>
 */
package com.demo.authcenter.store;
