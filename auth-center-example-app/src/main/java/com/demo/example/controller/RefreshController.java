package com.demo.example.controller;

import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.security.RefreshService;
import com.demo.example.dto.TokenPair;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RefreshController {

    private final RefreshService refreshService;
    private final JwtUtil jwtUtil;

    public RefreshController(RefreshService refreshService, JwtUtil jwtUtil) {
        this.refreshService = refreshService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用 refresh token 换新的一对 token（轮换）
     *
     * 传参方式（任选其一）：
     * 1) Authorization: Bearer <refreshToken>
     * 2) Body: {"refreshToken":"xxx"}（你想要我也能给）
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Missing refresh token in Authorization Bearer");
        }

        RefreshService.TokenPair pair = refreshService.rotate(refreshToken);

        // demo 返回你自己的 TokenPair
        return ResponseEntity.ok(new TokenPair(pair.accessToken(), pair.refreshToken()));
    }
}
