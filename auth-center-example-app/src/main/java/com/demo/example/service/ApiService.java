package com.demo.example.service;

import com.demo.authcenter.annotation.RequirePerm;
import com.demo.example.permission.ApiPermByStarter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 示例业务服务
 */
@Service
public class ApiService {
    /**
     *  Spring Security 自带权限校验
     *  Spring 会自动帮你加上 ROLE_ 前缀
     *  角色（Role）：粗粒度（管理员 / 普通用户）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminBySpring() {
        return Map.of(
                "status", "ok",
                "message", "admin task created by Spring"
        );
    }

    /**
     *  Spring Security 自带权限校验
     *  Spring 会自动帮你加上 ROLE_ 前缀
     *  权限（Authority / Permission）：细粒度（按钮 / 接口）
     */
    @PreAuthorize(
            "hasAuthority(T(com.demo.example.permission.ApiPermBySpring).QUERY.value())"
    )
    public Map<String, Object> queryBySpring() {
        return Map.of(
                "status", "ok",
                "message", "query-spring"
        );
    }

    /**
     *  Spring Security 自带权限校验
     *  Spring 会自动帮你加上 ROLE_ 前缀
     *  权限（Authority / Permission）：细粒度（按钮 / 接口）
     */
    @PreAuthorize(
            "hasAuthority(T(com.demo.example.permission.ApiPermBySpring).ADD.value())"
    )
    public Map<String, Object> addBySpring() {
        return Map.of(
                "status", "ok",
                "message", "add-spring"
        );
    }


    /**
     *  Starter 提供自定义权限校验
     *  角色（Role）：粗粒度（管理员 / 普通用户）
     */
    @RequirePerm(
            roles = {"ADMIN"}
    )
    public Map<String, Object> adminByStarter() {
        return Map.of(
                "status", "ok",
                "message", "admin-starter"
        );
    }

    /**
     *  Starter 提供自定义权限校验
     *  权限（Authority / Permission）：细粒度（按钮 / 接口）
     */
    @RequirePerm(
            perm = ApiPermByStarter.class,
            actions = {"QUERY"} ,
            roles = {"ADMIN"},
            mode = RequirePerm.Mode.ANY
    )
    public Map<String, Object> queryByStarter() {
        return Map.of(
                "status", "ok",
                "message", "query-starter"
        );
    }

    @RequirePerm(
            perm = ApiPermByStarter.class,
            actions = {"ADD"} ,
            roles = {"ADMIN"},
            mode = RequirePerm.Mode.ALL
    )
    public Map<String, Object> addByStarter() {
        return Map.of(
                "status", "ok",
                "message", "add-starter"
        );
    }
}
