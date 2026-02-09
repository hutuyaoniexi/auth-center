package com.demo.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例应用（example-app）的 Spring Security 相关配置。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>这是<b>业务系统自己的白名单</b>，用于在 SecurityConfig 中配置 permitAll。</li>
 *   <li>与 Starter 的 {@code auth-center.ignore-paths} 不同：
 *       starter 白名单用于“通用能力增强”；本配置用于“业务最终裁决”。</li>
 * </ul>
 *
 * <p><b>YAML 示例：</b></p>
 * <pre>
 * example-app:
 *   security:
 *     permit-all:
 *       - "/"
 *       - "/ping"
 *       - "/public/**"
 *       - "/swagger-ui/**"
 *       - "/v3/api-docs/**"
 * </pre>
 */
@ConfigurationProperties(prefix = "example-app.security")
public class SecurityProps {

    /**
     * 业务侧放行路径（白名单）。
     *
     * <p><b>匹配风格：</b>建议使用常见的 path pattern（如 /public/**）。</p>
     * <p><b>注意：</b>该配置本身不会自动生效，需要在 SecurityConfig 中显式使用。</p>
     */
    private List<String> permitAll = new ArrayList<>();

    public List<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(List<String> permitAll) {
        this.permitAll = permitAll;
    }

    /**
     * 将 permitAll 做一次防御性清洗：
     * - 过滤 null/空白
     * - 去重
     *
     * 方便 SecurityConfig 直接使用，避免重复写 stream 逻辑。
     */
    public String[] permitAllArray() {
        if (permitAll == null || permitAll.isEmpty()) {
            return new String[0];
        }
        return permitAll.stream()
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .toArray(String[]::new);
    }
}
