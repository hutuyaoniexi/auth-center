package com.demo.authcenter.exception;

/**
 * Token 缺失异常（内部语义异常）：
 *
 * <p>表示请求未携带 Authorization Bearer Token。</p>
 *
 * <p>设计约定：
 * <ul>
 *   <li>仅用于鉴权流程内部表达语义</li>
 *   <li>不直接映射 HTTP 响应</li>
 *   <li>对外 401 响应仍由 JwtAuthFilter 标记错误码，
 *       AuthenticationEntryPoint 统一输出</li>
 * </ul>
 */
public class TokenMissingException extends AuthException {

    public TokenMissingException(String message) {
        super(message);
    }
}
