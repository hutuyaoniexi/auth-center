package com.demo.authcenter.exception;

/**
 * Token 非法/不可用（内部语义异常）：
 * 包含但不限于签名错误、结构非法、过期、被拉黑等。
 *
 * <p>对外响应仍由 JwtAuthFilter + AuthenticationEntryPoint 统一输出（401 + 业务错误码）。</p>
 */
public class TokenInvalidException extends AuthException {

    public TokenInvalidException(String message) {
        super(message);
    }

    public TokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
