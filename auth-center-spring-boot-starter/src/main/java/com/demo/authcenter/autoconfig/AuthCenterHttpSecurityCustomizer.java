package com.demo.authcenter.autoconfig;

import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.web.handler.RestAccessDeniedHandler;
import com.demo.authcenter.web.handler.RestAuthenticationHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * AuthCenter 对 {@link HttpSecurity} 的“增量增强器”。
 *
 * <h3>设计定位</h3>
 * 本增强器只补齐“请求链路必需”的基础设施：JWT 过滤器 + 401/403 统一输出。</li>
 *
 * <h3>关于 401/403 输出</h3>
 * <ul>
 *   <li>401 由 {@link RestAuthenticationHandler} 负责，403 由 {@link RestAccessDeniedHandler} 负责。</li>
 *   <li>上述 handler 依赖 Jackson 相关组件；若业务未引入 Jackson，则不会装配 handler，本类会自动降级为 Spring Security 默认输出。</li>
 * </ul>
 */
public final class AuthCenterHttpSecurityCustomizer {

    private final ObjectProvider<JwtAuthFilter> jwtAuthFilterProvider;
    private final ObjectProvider<RestAuthenticationHandler> entryPointProvider;
    private final ObjectProvider<RestAccessDeniedHandler> deniedHandlerProvider;

    public AuthCenterHttpSecurityCustomizer(ObjectProvider<JwtAuthFilter> jwtAuthFilterProvider,
                                            ObjectProvider<RestAuthenticationHandler> entryPointProvider,
                                            ObjectProvider<RestAccessDeniedHandler> deniedHandlerProvider) {
        this.jwtAuthFilterProvider = jwtAuthFilterProvider;
        this.entryPointProvider = entryPointProvider;
        this.deniedHandlerProvider = deniedHandlerProvider;
    }


    public void customize(HttpSecurity http) throws Exception {

        // 1) 插入 JWT Filter（仅当业务提供 AuthUserService -> Starter 才会装配 JwtAuthFilter）
        JwtAuthFilter jwtAuthFilter = jwtAuthFilterProvider.getIfAvailable();
        if (jwtAuthFilter != null) {
            http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        }

        // 2) 配置 401/403 统一输出
        RestAuthenticationHandler entryPoint = entryPointProvider.getIfAvailable(); // 401
        RestAccessDeniedHandler deniedHandler = deniedHandlerProvider.getIfAvailable(); // 403

        // 若两者都不存在，则不做任何异常输出配置，自动降级为 Spring Security 默认行为
        if (entryPoint == null && deniedHandler == null) {
            return;
        }

        http.exceptionHandling(eh -> {
            if (entryPoint != null) {
                eh.authenticationEntryPoint(entryPoint);
            }
            if (deniedHandler != null) {
                eh.accessDeniedHandler(deniedHandler);
            }
        });
    }
}
