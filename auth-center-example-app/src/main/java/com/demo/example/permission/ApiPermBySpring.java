package com.demo.example.permission;

/**
 * Spring 方法级注解的权限示例
 * 统一格式：resource:action
 */
public enum ApiPermBySpring {
    QUERY("api:query", "查询接口"),
    ADD("api:add", "新增接口"),
    UPDATE("api:update", "修改接口"),
    DELETE("api:delete", "删除接口");

    private final String code;
    private final String desc;

    ApiPermBySpring(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String value() {
        return code;
    }

    public String desc() {
        return desc;
    }
}
