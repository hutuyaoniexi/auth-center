package com.demo.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 只要通过鉴权即可访问
     * ROLE_USER / ROLE_ADMIN 都可以
     */
    @GetMapping("/me")
    public Map<String, Object> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }

    @GetMapping("/query")
    @PreAuthorize("hasAuthority('user:query')")
    public Map<String, Object> query() {
        return Map.of(
                "status", "ok",
                "message", "query"
        );
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:add')")
    public Map<String, Object>  add() {
        return Map.of(
                "status", "ok",
                "message", "add"
        );
    }
}
