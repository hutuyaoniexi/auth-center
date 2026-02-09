package com.demo.example.controller;

import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.security.RefreshService;
import com.demo.authcenter.store.TokenStore;
import com.demo.authcenter.security.dto.JwtTokenPair;
import com.demo.authcenter.spi.AuthUser;
import com.demo.authcenter.spi.AuthUserService;
import com.demo.example.dto.LoginRequest;
import com.demo.example.dto.TokenPair;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 鉴权接口
 */
@Tag(name = "Auth", description = "鉴权相关：登录、刷新、登出（JWT access/refresh）")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;
    private final RefreshService refreshService;
    private final TokenStore tokenStore;

    public AuthController(JwtUtil jwtUtil,
                          AuthUserService authUserService,
                          RefreshService refreshService,
                          TokenStore tokenStore) {
        this.jwtUtil = jwtUtil;
        this.authUserService = authUserService;
        this.refreshService = refreshService;
        this.tokenStore = tokenStore;
    }

    @Operation(
            summary = "登录并签发 TokenPair",
            description = "使用用户名（demo）加载用户，签发 accessToken + refreshToken。"
    )
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginRequest req) {
        AuthUser user = authUserService.loadByUsername(req.username());

        String access = jwtUtil.generateAccessToken(
                user.userId(),
                user.username(),
                user.authorities()
        );
        String refresh = jwtUtil.generateRefreshToken(user.userId());

        return ResponseEntity.ok(new TokenPair(access, refresh));
    }

    @Operation(
            summary = "登出",
            description = "将当前 access token 的 jti 加入黑名单，直到 token 过期。"
    )
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {

        // 从 Authorization 头中取 access token
        String token = jwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (token == null) {
            return Map.of(
                    "status", "error",
                    "message", "Missing Authorization Bearer token"
            );
        }

        // 解析 token
        Claims claims = jwtUtil.parseAndValidate(token);

        // 确保是 access token（防止拿 refresh 来登出）
        jwtUtil.validateAccessType(claims);

        // 取 jti（JWT ID token的唯一标识）
        String jti = jwtUtil.getJti(claims);
        if (jti == null || jti.isBlank()) {
            return Map.of(
                    "status", "error",
                    "message", "Missing jti in token"
            );
        }

        // 拉黑到 token 原始过期时间
        tokenStore.blacklist(jti, claims.getExpiration().toInstant());

        return Map.of(
                "status", "ok",
                "message", "已退出"
        );
    }

    @Operation(
            summary = "刷新 TokenPair（轮换）",
            description = "使用 refresh token 换取新的一对 access/refresh token（rotate）。传参：Authorization: Bearer <refreshToken>"
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Missing refresh token in Authorization Bearer");
        }

        JwtTokenPair pair = refreshService.rotate(refreshToken);

        // demo 返回你自己的 TokenPair
        return ResponseEntity.ok(new TokenPair(pair.accessToken(), pair.refreshToken()));
    }
}
