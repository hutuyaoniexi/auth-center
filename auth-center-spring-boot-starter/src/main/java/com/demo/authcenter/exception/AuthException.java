package com.demo.authcenter.exception;

/**
 * 鉴权模块统一异常父类（Auth-Center 内部使用）：
 *
 * <p>表示鉴权流程中<strong>可预期的业务异常</strong>，
 * 用于区分于系统异常（NPE / IO / DB 等）。</p>
 *
 * <p>设计约定：
 * <ul>
 *   <li>不直接映射 HTTP 状态码</li>
 *   <li>不直接作为 Controller 对外异常抛出</li>
 *   <li>通常在 Service / Filter / AOP 层抛出，由上层统一处理</li>
 * </ul>
 *
 * <p>示例场景：
 * <ul>
 *   <li>Token 状态非法</li>
 *   <li>Refresh token 已被使用</li>
 *   <li>权限模型配置错误</li>
 * </ul>
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
