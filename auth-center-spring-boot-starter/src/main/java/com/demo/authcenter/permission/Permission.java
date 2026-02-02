package com.demo.authcenter.permission;

public interface Permission {

    /**
     * 权限唯一标识（最终进 JWT / Authority 的字符串）
     */
    String value();
}
