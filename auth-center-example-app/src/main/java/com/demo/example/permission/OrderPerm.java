package com.demo.example.permission;

import com.demo.authcenter.permission.Permission;

public enum OrderPerm implements Permission {

    READ("perm:order:read"),
    CREATE("perm:order:create"),
    UPDATE("perm:order:update"),
    DELETE("perm:order:delete");

    private final String value;

    OrderPerm(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
