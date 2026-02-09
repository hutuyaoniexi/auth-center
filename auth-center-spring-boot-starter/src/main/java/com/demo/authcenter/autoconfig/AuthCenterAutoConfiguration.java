package com.demo.authcenter.autoconfig;

import com.demo.authcenter.properties.JwtProps;
import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.security.LogoutService;
import com.demo.authcenter.security.RefreshService;
import com.demo.authcenter.spi.AuthUserService;
import com.demo.authcenter.store.InMemoryTokenStore;
import com.demo.authcenter.store.TokenStore;
import com.demo.authcenter.web.handler.RestAccessDeniedHandler;
import com.demo.authcenter.web.handler.RestAuthenticationHandler;
import com.demo.authcenter.web.response.JsonResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * AuthCenter Starter 主自动装配入口（Web 层）。
 * <p>
 *
 * 设计定位：
 * 本 Starter只提供 {@link AuthCenterHttpSecurityCustomizer} 作为“增量增强器”，由业务系统在自身的 Security 配置中
 * 显式调用以完成鉴权链路装配。
 * </p>
 *
 * 本类负责装配的能力：
 * <ol>
 *   <li>读取配置：{@link JwtProps}</li>
 *   <li>JWT 工具：{@link JwtUtil}</li>
 *   <li>Token 状态存储：{@link TokenStore}（默认 {@link InMemoryTokenStore}）</li>
 *   <li>JWT 请求过滤器：{@link JwtAuthFilter}（仅当业务提供 {@link AuthUserService} 时装配）</li>
 *   <li>统一 401/403 JSON 输出：{@link JsonResponseWriter}/{@link RestAuthenticationHandler}/{@link RestAccessDeniedHandler}
 *       （依赖 Jackson，可选）</li>
 *   <li>HttpSecurity 增强器：{@link AuthCenterHttpSecurityCustomizer}</li>
 * </ol>
 */
@AutoConfiguration
@EnableConfigurationProperties({JwtProps.class})
@ConditionalOnProperty(prefix = "auth-center", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(HttpSecurity.class)
public class AuthCenterAutoConfiguration {

    // ==================== JWT 基础能力 ====================

    /**
     * JWT 核心工具类：封装 token 生成/解析/校验。
     * <p>业务方可通过自定义 {@link JwtUtil} Bean 覆盖默认实现。</p>
     */
    @Bean
    @ConditionalOnMissingBean(JwtUtil.class)
    public JwtUtil jwtUtil(JwtProps jwtProps) {
        return new JwtUtil(jwtProps);
    }

    // ==================== Token 状态存储（Filter 依赖） ====================

    /**
     * Token 状态存储：用于管理 token 生命周期（登出失效、踢下线、refresh 轮换等）。
     * <p>默认内存实现适用于单机/示例；生产通常替换为 Redis 等集中式存储。</p>
     */
    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    // ==================== 请求入口：JWT Filter ====================

    /**
     * JWT 鉴权过滤器（仅当业务系统提供 {@link AuthUserService} 时才装配）：
     * - 提取 Bearer Token
     * - 校验 token（签名/过期/状态）
     * - 加载用户与权限并写入 SecurityContext
     */
    @Bean
    @ConditionalOnBean(AuthUserService.class)
    @ConditionalOnMissingBean(JwtAuthFilter.class)
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil,
                                       TokenStore tokenStore,
                                       AuthUserService authUserService)
    throws Exception {
        return new JwtAuthFilter(jwtUtil, tokenStore, authUserService);
    }

    // ==================== 统一 JSON 输出（401 / 403，可选）====================

    /**
     * 安全异常 JSON 响应写入器（依赖 Jackson，可选）。
     * <p>若项目未引入 Jackson，则不装配该组件；401/403 输出将降级为 Spring Security 默认行为。</p>
     */
    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(JsonResponseWriter.class)
    public JsonResponseWriter jsonResponseWriter(ObjectMapper objectMapper) {
        return new JsonResponseWriter(objectMapper);
    }

    /** 401：未认证（缺 token / token 无效 / token 过期 / 被踢下线等）。 */
    @Bean
    @ConditionalOnBean(JsonResponseWriter.class)
    @ConditionalOnMissingBean(RestAuthenticationHandler.class)
    public RestAuthenticationHandler restAuthenticationEntryPoint(JsonResponseWriter writer) {
        return new RestAuthenticationHandler(writer);
    }

    /** 403：已认证但无权限。 */
    @Bean
    @ConditionalOnBean(JsonResponseWriter.class)
    @ConditionalOnMissingBean(RestAccessDeniedHandler.class)
    public RestAccessDeniedHandler restAccessDeniedHandler(JsonResponseWriter writer) {
        return new RestAccessDeniedHandler(writer);
    }

    // ==================== HttpSecurity 增强器（A 模式） ====================

    /**
     * HttpSecurity 增强器：由业务系统显式调用完成安全链路装配。
     *
     * <p>仅补齐：JWT Filter + 401/403 输出（若可用）。
     * 不接管：授权规则 / CSRF / permitAll / Session 策略。</p>
     */
    @Bean
    @ConditionalOnMissingBean(AuthCenterHttpSecurityCustomizer.class)
    public AuthCenterHttpSecurityCustomizer authCenterHttpSecurityCustomizer(
            ObjectProvider<JwtAuthFilter> jwtAuthFilterProvider,
            ObjectProvider<RestAuthenticationHandler> entryPointProvider,
            ObjectProvider<RestAccessDeniedHandler> deniedHandlerProvider
    ) {
        return new AuthCenterHttpSecurityCustomizer(
                jwtAuthFilterProvider,
                entryPointProvider,
                deniedHandlerProvider
        );
    }

    // ==================== 业务可调用服务 ====================

    /** 登出服务：通常将当前 token 标记为失效（写入 TokenStore）。 */
    @Bean
    @ConditionalOnMissingBean(LogoutService.class)
    public LogoutService logoutService(JwtUtil jwtUtil, TokenStore tokenStore) {
        return new LogoutService(jwtUtil, tokenStore);
    }

    /**
     * 刷新 token 服务（可选）：
     * - 开关：auth-center.jwt.refresh-enabled=true
     * - 依赖业务侧 {@link AuthUserService}
     */
    @Bean
    @ConditionalOnBean(AuthUserService.class)
    @ConditionalOnProperty(prefix = "auth-center.jwt", name = "refresh-enabled", havingValue = "true")
    @ConditionalOnMissingBean(RefreshService.class)
    public RefreshService refreshService(JwtUtil jwtUtil,
                                         JwtProps jwtProps,
                                         TokenStore tokenStore,
                                         AuthUserService authUserService) {
        return new RefreshService(jwtUtil, jwtProps, tokenStore, authUserService);
    }

    static {
        System.out.println(">>> AuthCenterMethodSecurityAutoConfiguration loaded");
    }
}
