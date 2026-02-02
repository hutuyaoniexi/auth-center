package com.demo.authcenter.web.handler;

import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.web.response.ApiError;
import com.demo.authcenter.web.response.JsonResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonResponseWriter writer;

    public RestAuthenticationEntryPoint(JsonResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 默认：未携带 Token
        int code = JwtAuthFilter.CODE_TOKEN_MISSING;

        Object attr = request.getAttribute(JwtAuthFilter.REQ_ATTR_AUTH_ERROR_CODE);
        if (attr instanceof Integer i) {
            code = i;
        }

        String message = switch (code) {
            case JwtAuthFilter.CODE_TOKEN_EXPIRED -> "Token已过期";
            case JwtAuthFilter.CODE_TOKEN_INVALID -> "Token非法或签名错误";
            default -> "未认证或Token缺失";
        };

        ApiError body = ApiError.of(code, message, request.getRequestURI());

        // ✅ 使用你现有的 writer.write(...)
        writer.write(response, HttpServletResponse.SC_UNAUTHORIZED, body);
    }
}
