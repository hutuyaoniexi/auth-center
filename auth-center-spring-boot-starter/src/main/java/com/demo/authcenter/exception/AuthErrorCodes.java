package com.demo.authcenter.exception;

/**
 * Auth-Center 错误码常量：
 * - code 为业务错误码（与 HTTP 状态码区分）
 * - request attribute 用于 Filter -> EntryPoint 传递认证失败原因
 *
 * 约定：
 * - 401xx：认证失败（未认证/Token 无效）
 * - 403xx：授权失败（已认证但无权限）
 */
public final class AuthErrorCodes {

    private AuthErrorCodes() {}

    /** Filter 写入 request attribute 的 key：用于把认证失败原因传给 EntryPoint */
    public static final String REQ_ATTR_AUTH_ERROR_CODE = "AUTH_CENTER_AUTH_ERROR_CODE";

    // ===== 401xx：认证失败 =====
    public static final int CODE_TOKEN_MISSING = 40101;
    public static final int CODE_TOKEN_EXPIRED = 40102;
    public static final int CODE_TOKEN_INVALID = 40103;
    public static final int CODE_TOKEN_BLACKLISTED = 40104;

    // ===== 403xx：授权失败 =====
    public static final int CODE_FORBIDDEN = 40301;
}
