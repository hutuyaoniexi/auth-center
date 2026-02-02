package com.demo.authcenter.autoconfig;

import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.security.*;
import com.demo.authcenter.web.handler.RestAccessDeniedHandler;
import com.demo.authcenter.web.handler.RestAuthenticationEntryPoint;
import com.demo.authcenter.web.response.JsonResponseWriter;
import com.demo.authcenter.spi.AuthUserService;
import com.demo.authcenter.store.InMemoryTokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@AutoConfiguration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProps.class, AuthCenterProperties.class})
@ConditionalOnProperty(prefix = "auth.center", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(HttpSecurity.class)
public class AuthCenterAutoConfiguration {

    // -------------------- 基础配置 --------------------

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(JwtProps jwtProps) {
        return new JwtUtil(jwtProps);
    }

    /**
     * 只有业务系统提供了 AuthUserService（SPI）我们才启用 JWT Filter
     */
    @Bean
    @ConditionalOnBean(AuthUserService.class)
    @ConditionalOnMissingBean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil,
                                       TokenStore tokenStore,
                                       AuthUserService authUserService,
                                       AuthCenterProperties props) {
        return new JwtAuthFilter(jwtUtil, tokenStore, authUserService, props);
    }

    // -------------------- 统一 JSON 输出（401/403）--------------------

    @Bean
    @ConditionalOnMissingBean
    public JsonResponseWriter jsonResponseWriter(ObjectMapper objectMapper) {
        return new JsonResponseWriter(objectMapper);
    }

    /**
     * 401：未认证 / Token 无效
     */
    @Bean
    @ConditionalOnMissingBean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint(JsonResponseWriter writer) {
        return new RestAuthenticationEntryPoint(writer);
    }

    /**
     * 403：已认证但无权限
     */
    @Bean
    @ConditionalOnMissingBean
    public RestAccessDeniedHandler restAccessDeniedHandler(JsonResponseWriter writer) {
        return new RestAccessDeniedHandler(writer);
    }

    // -------------------- 提供给业务系统调用的增强器（不抢全局）--------------------

    /**
     * Starter 不创建 SecurityFilterChain，只提供 customizer 给业务系统调用：
     * - 挂 JWT Filter
     * - 挂 401/403 标准化 handler
     * - 可选：按 props 注入白名单 permitAll
     */
    @Bean
    @ConditionalOnBean(JwtAuthFilter.class)
    @ConditionalOnMissingBean
    public AuthCenterHttpSecurityCustomizer authCenterHttpSecurityCustomizer(
            JwtAuthFilter jwtAuthFilter,
            AuthCenterProperties props,
            RestAuthenticationEntryPoint entryPoint,
            RestAccessDeniedHandler deniedHandler
    ) {
        return new AuthCenterHttpSecurityCustomizer(jwtAuthFilter, props, entryPoint, deniedHandler);
    }


    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutService logoutService(JwtUtil jwtUtil, TokenStore tokenStore) {
        return new LogoutService(jwtUtil, tokenStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "auth.jwt", name = "refresh-enabled", havingValue = "true")
    public RefreshService refreshService(JwtUtil jwtUtil,
                                         JwtProps jwtProps,
                                         TokenStore tokenStore,
                                         AuthUserService authUserService) {
        return new RefreshService(jwtUtil, jwtProps, tokenStore, authUserService);
    }


}
