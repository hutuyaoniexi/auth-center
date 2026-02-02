package com.demo.example.controller;

import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.spi.AuthUser;
import com.demo.authcenter.spi.AuthUserService;
import com.demo.example.dto.LoginRequest;
import com.demo.example.dto.TokenPair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;

    public AuthController(JwtUtil jwtUtil,
                          AuthUserService authUserService) {
        this.jwtUtil = jwtUtil;
        this.authUserService = authUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        // 1️⃣ 登录阶段：通过 username 加载用户
        AuthUser user = authUserService.loadByUsername(req.username());

        // 2️⃣ 签发 access token（sub = userId）
        String access =
                jwtUtil.generateAccessToken(
                        user.userId(),
                        user.username(),
                        user.authorities()
                );

        // 3️⃣ 签发 refresh token（如果启用）
        String refresh =
                jwtUtil.generateRefreshToken(user.userId());

        // 4️⃣ 返回 token 对
        return ResponseEntity.ok(new TokenPair(access, refresh));
    }
}
