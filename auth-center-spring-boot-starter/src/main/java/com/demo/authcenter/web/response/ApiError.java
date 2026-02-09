package com.demo.authcenter.web.response;

import java.time.OffsetDateTime;

/**
 * 标准错误响应模型（REST）：
 *
 * <p>用于统一封装鉴权/授权失败等错误的返回结构。</p>
 *
 * <p>字段说明：
 * <ul>
 *   <li>timestamp：错误发生时间（服务端时间）</li>
 *   <li>code：业务错误码（如 Token 过期/非法/无权限）</li>
 *   <li>message：面向调用方的错误描述</li>
 *   <li>path：触发错误的请求路径</li>
 * </ul>
 */
public class ApiError {

    private final OffsetDateTime timestamp;
    private final int code;
    private final String message;
    private final String path;

    private ApiError(int code, String message, String path) {
        this.timestamp = OffsetDateTime.now();
        this.code = code;
        this.message = message;
        this.path = path;
    }

    /**
     * 创建标准错误响应对象。
     */
    public static ApiError of(int code, String message, String path) {
        return new ApiError(code, message, path);
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
