package com.demo.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * 仅允许 ADMIN 角色访问
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/task")
    public Map<String, Object> createTask() {
        return Map.of(
                "status", "ok",
                "message", "admin task created"
        );
    }
}

