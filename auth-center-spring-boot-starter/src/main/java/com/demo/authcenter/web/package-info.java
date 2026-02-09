/**
 * Web 层支持：统一错误响应与写出工具。
 *
 * <p>本包用于保证鉴权模块在 401/403 场景下输出一致的 JSON 响应体，便于前端统一处理。</p>
 *
 * <p>通常包含：</p>
 * <ul>
 *   <li>AuthenticationEntryPoint：401 未认证/Token 无效</li>
 *   <li>AccessDeniedHandler：403 已认证但无权限</li>
 *   <li>ApiError / JsonResponseWriter：统一响应模型与写出工具</li>
 * </ul>
 */
package com.demo.authcenter.web;
