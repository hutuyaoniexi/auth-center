/**
 * 鉴权核心实现：JWT 与刷新/登出等服务。
 *
 * <p>本包提供 JWT 的生成、解析与校验能力，并定义 refresh / logout 等核心流程。</p>
 *
 * <h2>为什么 JWT 需要 TokenStore？</h2>
 * <p>JWT 天生无状态，仅靠签名无法实现“主动失效”。为支持以下场景，需要引入状态存储：</p>
 * <ul>
 *   <li>登出：使当前 token 立即失效</li>
 *   <li>踢下线：使指定用户的 token 失效</li>
 *   <li>刷新 rotate：旧 refresh/access 链路作废，防止重放攻击</li>
 * </ul>
 *
 * <p>因此模块通过 TokenStore（按 jti 管理）实现 JWT 的可控失效能力。</p>
 */
package com.demo.authcenter.security;
