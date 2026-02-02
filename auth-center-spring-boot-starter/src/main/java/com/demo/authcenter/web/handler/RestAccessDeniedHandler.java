package com.demo.authcenter.web.handler;

import com.demo.authcenter.web.response.ApiError;
import com.demo.authcenter.web.response.JsonResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    public static final int CODE_FORBIDDEN = 40301;

    private final JsonResponseWriter writer;

    public RestAccessDeniedHandler(JsonResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ApiError body = ApiError.of(
                CODE_FORBIDDEN,
                "权限不足",
                request.getRequestURI()
        );

        writer.write(response, HttpServletResponse.SC_FORBIDDEN, body);
    }
}
