package com.demo.example.permission;

import com.demo.authcenter.permission.PermissionAction;

/**
 * Starter 自定义注解的权限定义
 * 统一格式：resource:action
 */
public enum ApiPermByStarter implements PermissionAction {

    QUERY("api:query"),
    ADD("api:add"),
    UPDATE("api:update"),
    DELETE("api:delete");

    private final String value;

    ApiPermByStarter(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
