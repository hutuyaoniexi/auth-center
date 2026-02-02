package com.demo.example.config;

import com.demo.authcenter.autoconfig.AuthCenterHttpSecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthCenterHttpSecurityCustomizer authCenterCustomizer
    ) throws Exception {

        // 1) 业务系统自己的基础策略（你原来怎么配就怎么配）
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(hb -> hb.disable())
                .formLogin(fl -> fl.disable())
                .anonymous(Customizer.withDefaults());

        // 2) ✅ 核心：让 Starter 把 JWT Filter + 401/403 标准化挂到这条链上
        //    注意：这一步不应该改变你 anyRequest 的策略，只是增强
        authCenterCustomizer.customize(http);

        // 3) 业务系统的授权规则（你自己决定）
        //    如果你希望“未登录访问 /api/hello -> 401”，这里必须让它走 authenticated()
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/test/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
