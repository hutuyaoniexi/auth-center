package com.demo.authcenter.security.config;

import com.demo.authcenter.filter.JwtAuthFilter;
import com.demo.authcenter.web.handler.RestAccessDeniedHandler;
import com.demo.authcenter.web.handler.RestAuthenticationEntryPoint;
import com.demo.authcenter.web.response.JsonResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthCenterSecurityConfiguration {

    @Bean
    public JsonResponseWriter jsonResponseWriter(ObjectMapper objectMapper) {
        return new JsonResponseWriter(objectMapper);
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint(JsonResponseWriter writer) {
        return new RestAuthenticationEntryPoint(writer);
    }

    @Bean
    public RestAccessDeniedHandler restAccessDeniedHandler(JsonResponseWriter writer) {
        return new RestAccessDeniedHandler(writer);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RestAuthenticationEntryPoint entryPoint,
            RestAccessDeniedHandler deniedHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 关键：明确授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行你测试的 token 接口 / 健康检查等（按你项目实际补充）
                        .requestMatchers("/test/**", "/public/**", "/actuator/**").permitAll()
                        // 其他全部需要认证
                        .anyRequest().authenticated()
                )

                // ✅ 关键：确保匿名用户存在（未登录时能触发 401 EntryPoint）
                .anonymous(Customizer.withDefaults())

                // ✅ 关键：挂 401 / 403 标准化处理器
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint) // 401
                        .accessDeniedHandler(deniedHandler)   // 403
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
