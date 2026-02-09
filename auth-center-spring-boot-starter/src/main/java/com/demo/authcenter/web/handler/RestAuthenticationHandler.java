package com.demo.authcenter.web.handler;

import com.demo.authcenter.exception.AuthErrorCodes;
import com.demo.authcenter.web.response.ApiError;
import com.demo.authcenter.web.response.JsonResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.Objects;

/**
 * 401 认证失败处理器（REST）：
 *
 * <p>用于处理未认证或 Token 无效的场景，统一返回 JSON 错误体。</p>
 *
 * <p>判定顺序：
 * <ol>
 *   <li>优先读取 JwtAuthFilter 写入的错误码（expired / invalid / blacklisted）</li>
 *   <li>若无错误码，则根据 Authorization Header 兜底判断：
 *     <ul>
 *       <li>无 Bearer Token：TOKEN_MISSING</li>
 *       <li>有 Bearer 但未被标记：TOKEN_INVALID</li>
 *     </ul>
 *   </li>
 * </ol>
 */
public class RestAuthenticationHandler implements AuthenticationEntryPoint {

    private final JsonResponseWriter writer;

    public RestAuthenticationHandler(JsonResponseWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        int code = resolveCode(request);
        String message = resolveMessage(code);

        ApiError body = ApiError.of(code, message, request.getRequestURI());

        try {
            writer.write(response, HttpServletResponse.SC_UNAUTHORIZED, body);
        } catch (Exception ignore) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private int resolveCode(HttpServletRequest request) {
        Object attr = request.getAttribute(AuthErrorCodes.REQ_ATTR_AUTH_ERROR_CODE);

        if (attr instanceof Integer i) {
            return i;
        }
        if (attr instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignore) {
            }
        }

        // Filter 未标记时的兜底逻辑
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank() || !auth.startsWith("Bearer ")) {
            return AuthErrorCodes.CODE_TOKEN_MISSING;
        }
        String token = auth.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            return AuthErrorCodes.CODE_TOKEN_MISSING;
        }
        return AuthErrorCodes.CODE_TOKEN_INVALID;
    }

    private String resolveMessage(int code) {
        return switch (code) {
            case AuthErrorCodes.CODE_TOKEN_EXPIRED -> "Token已过期";
            case AuthErrorCodes.CODE_TOKEN_INVALID -> "Token非法或签名错误";
            case AuthErrorCodes.CODE_TOKEN_BLACKLISTED -> "Token已失效（已登出或被踢下线）";
            case AuthErrorCodes.CODE_TOKEN_MISSING -> "未认证或Token缺失";
            default -> "未认证";
        };
    }
}
