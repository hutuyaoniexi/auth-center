package com.demo.authcenter.exception;

/**
 * 权限不足异常：
 *
 * <p>用于鉴权流程中的<strong>权限判定失败</strong>场景，
 * 例如 @RequirePerm 在 AOP 层校验未通过。</p>
 *
 * <p>设计约定：
 * <ul>
 *   <li>仅用于 Starter 内部控制流程</li>
 *   <li>不直接映射 HTTP 响应</li>
 *   <li>最终仍由统一的 AccessDeniedHandler 输出 403</li>
 * </ul>
 */
public class PermissionDeniedException extends AuthException {

    public PermissionDeniedException(String message) {
        super(message);
    }
}
