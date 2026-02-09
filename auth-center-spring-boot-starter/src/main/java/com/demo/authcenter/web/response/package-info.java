/**
 * 统一响应模型与写出工具。
 *
 * <p>本包用于封装统一 JSON 输出结构，例如 ApiError、以及将对象写入 HttpServletResponse 的工具类。</p>
 *
 * <p>建议：</p>
 * <ul>
 *   <li>鉴权相关错误统一使用 ApiError 输出，并携带业务错误码（401xx/403xx）</li>
 *   <li>JsonResponseWriter 作为唯一写出入口，避免散落 response.getWriter() 导致格式不一致</li>
 * </ul>
 */
package com.demo.authcenter.web.response;
