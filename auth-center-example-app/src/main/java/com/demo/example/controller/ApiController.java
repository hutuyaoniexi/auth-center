package com.demo.example.controller;

import com.demo.example.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 业务接口
 */
@Tag(name = "API", description = "示例业务接口：公开接口、登录可访问接口、角色/权限控制（Spring vs Starter）")
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    // -------------------- Public --------------------

    @Operation(
            summary = "对外开放：Hello",
            description = "无需登录即可访问，用于连通性测试。"
    )
    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        return "hello";
    }

    // -------------------- Authenticated --------------------

    @Operation(
            summary = "当前登录用户信息",
            description = "需要 Bearer Token，返回 username 与 authorities。"
            , security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }

    // -------------------- Spring Security (role/perm) --------------------

    @Operation(
            summary = "仅 ADMIN 角色可访问（Spring）",
            description = "需要 ROLE_ADMIN（示例：@PreAuthorize/hasRole）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "/admin/spring", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> adminBySpring() {
        return apiService.adminBySpring();
    }

    @Operation(
            summary = "仅具备查询权限可访问（Spring）",
            description = "需要查询权限（示例：perm:query）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/query/spring", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> queryBySpring() {
        return apiService.queryBySpring();
    }

    @Operation(
            summary = "仅具备添加权限可访问（Spring）",
            description = "需要添加权限（示例：perm:add）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "/add/spring", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> addBySpring() {
        return apiService.addBySpring();
    }

    // -------------------- Starter Custom Permission --------------------

    @Operation(
            summary = "仅 ADMIN 角色可访问（Starter）",
            description = "Starter 自定义鉴权：角色校验（示例：@RequirePerm/@RequireRole）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "/admin/starter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> adminByStarter() {
        return apiService.adminByStarter();
    }

    @Operation(
            summary = "仅具备查询权限可访问（Starter）",
            description = "Starter 自定义鉴权：权限校验（示例：perm:query）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/query/starter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> queryByStarter() {
        return apiService.queryByStarter();
    }

    @Operation(
            summary = "仅具备添加权限可访问（Starter）",
            description = "Starter 自定义鉴权：权限校验（示例：perm:add）。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "/add/starter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> addByStarter() {
        return apiService.addByStarter();
    }



    @Bean
    CommandLineRunner printMethodMode(Environment env) {
        return args -> {
            System.out.println("auth-center.method-security-mode = "
                    + env.getProperty("auth-center.method-security-mode"));

            System.out.println("ENV AUTH_CENTER_METHOD_SECURITY_MODE = "
                    + System.getenv("AUTH_CENTER_METHOD_SECURITY_MODE"));
        };
    }

}

