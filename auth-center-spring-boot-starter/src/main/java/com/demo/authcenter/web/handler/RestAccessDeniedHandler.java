package com.demo.authcenter.web.handler;

import com.demo.authcenter.exception.AuthErrorCodes;
import com.demo.authcenter.web.response.ApiError;
import com.demo.authcenter.web.response.JsonResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Objects;

/**
 * 403 处理器：已认证但无权限时返回统一 JSON。
 *
 * <p>触发场景：@PreAuthorize / @RequirePerm 等授权失败。</p>
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonResponseWriter writer;

    public RestAccessDeniedHandler(JsonResponseWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) {

        ApiError body = ApiError.of(
                AuthErrorCodes.CODE_FORBIDDEN,
                "权限不足",
                request.getRequestURI()
        );

        try {
            writer.write(response, HttpServletResponse.SC_FORBIDDEN, body);
        } catch (Exception ignore) {
            // writer 失败时至少保证状态码正确
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
