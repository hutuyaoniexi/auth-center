package com.demo.authcenter.autoconfig;

import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.security.AuthCenterProperties;
import com.demo.authcenter.web.handler.RestAccessDeniedHandler;
import com.demo.authcenter.web.handler.RestAuthenticationEntryPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

public class AuthCenterHttpSecurityCustomizer {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthCenterProperties props;
    private final RestAuthenticationEntryPoint entryPoint;
    private final RestAccessDeniedHandler deniedHandler;

    public AuthCenterHttpSecurityCustomizer(
            JwtAuthFilter jwtAuthFilter,
            AuthCenterProperties props,
            RestAuthenticationEntryPoint entryPoint,
            RestAccessDeniedHandler deniedHandler
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.props = props;
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    public void customize(HttpSecurity http) throws Exception {

        // 1) 401/403 标准化
        http.exceptionHandling(eh -> eh
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
        );

        // 2) JWT Filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 3) 统一白名单（不抢业务系统全局策略，只做 permitAll 增强）
        List<String> ignorePaths = props.getIgnorePaths();
        if (ignorePaths != null && !ignorePaths.isEmpty()) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(ignorePaths.toArray(new String[0])).permitAll()
            );
        }
    }
}
