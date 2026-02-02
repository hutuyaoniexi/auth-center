package com.demo.authcenter.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "auth.center")
public class AuthCenterProperties {

    /**
     * 是否启用鉴权模块
     */
    private boolean enabled = true;

    /**
     * 忽略鉴权的路径（白名单）
     * 例如：/public/**、/test/**、/actuator/**
     */
    private List<String> ignorePaths = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getIgnorePaths() {
        return ignorePaths;
    }

    public void setIgnorePaths(List<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }
}
