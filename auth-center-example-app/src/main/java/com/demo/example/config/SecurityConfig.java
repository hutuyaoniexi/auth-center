package com.demo.example.config;

import com.demo.authcenter.autoconfig.AuthCenterHttpSecurityCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * 业务系统 Spring Security 配置（业务侧负责安全策略，Starter 只做“链路补齐”）。
 *
 * <p>职责边界：</p>
 * <ul>
 *   <li>业务侧：CSRF / Session / Basic / FormLogin / 授权规则（permitAll、anyRequest等）</li>
 *   <li>Starter：JWT Filter + 401/403 统一输出（由 {@link AuthCenterHttpSecurityCustomizer} 增量增强）</li>
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(SecurityProps.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<AuthCenterHttpSecurityCustomizer> authCenterCustomizerProvider,
            SecurityProps props
    ) throws Exception {

        // 1) 业务系统全局基础策略（业务侧统一负责，避免与 starter 重复/冲突）
        http
                // JWT 场景一般是无状态 API：禁用 CSRF + 无 Session
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 关闭默认认证方式，避免出现“generated security password / 默认登录页”
                .httpBasic(hb -> hb.disable())
                .formLogin(fl -> fl.disable());

        // 2) 可选：让 Starter 增量增强 HttpSecurity（只补齐 JWT Filter + 401/403 输出）
        AuthCenterHttpSecurityCustomizer authCenterCustomizer = authCenterCustomizerProvider.getIfAvailable();
        if (authCenterCustomizer != null) {
            authCenterCustomizer.customize(http);
        }

        // 3) 业务系统自己的授权规则（permitAll + 其余全部鉴权）
        http.authorizeHttpRequests(auth -> {
            List<String> permitAll = props.getPermitAll();
            if (permitAll != null && !permitAll.isEmpty()) {

                // 配置防御：过滤空白/去重（这些是路径模式，如 /swagger-ui/**）
                String[] whitelist = permitAll.stream()
                        .filter(p -> p != null && !p.isBlank())
                        .distinct()
                        .toArray(String[]::new);

                if (whitelist.length > 0) {
                    auth.requestMatchers(whitelist).permitAll();
                }
            }

            auth.anyRequest().authenticated();
        });

        return http.build();
    }
}
