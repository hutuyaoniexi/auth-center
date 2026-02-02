package com.demo.example.auth;

import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.security.TokenStore;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public LogoutController(JwtUtil jwtUtil, TokenStore tokenStore) {
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
    }

    /**
     * 登出：把当前 access token 的 jti 拉黑
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {

        // 1️⃣ 从 Authorization 头中取 access token
        String token = jwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (token == null) {
            return Map.of(
                    "status", "error",
                    "message", "Missing Authorization Bearer token"
            );
        }

        // 2️⃣ 解析 token（校验签名 / exp / iss）
        Claims claims = jwtUtil.parseAndValidate(token);

        // 3️⃣ 确保是 access token（防止拿 refresh 来登出）
        jwtUtil.validateAccessType(claims);

        // 4️⃣ 取 jti + exp
        String jti = jwtUtil.getJti(claims);
        if (jti == null || jti.isBlank()) {
            return Map.of(
                    "status", "error",
                    "message", "Missing jti in token"
            );
        }

        // 5️⃣ 拉黑到 token 原始过期时间
        tokenStore.blacklist(jti, claims.getExpiration().toInstant());

        return Map.of(
                "status", "ok",
                "message", "已退出"
        );
    }
}
