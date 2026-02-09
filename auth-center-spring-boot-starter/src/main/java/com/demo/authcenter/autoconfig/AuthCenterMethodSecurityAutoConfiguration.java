package com.demo.authcenter.autoconfig;

import com.demo.authcenter.aop.RequirePermAspect;
import com.demo.authcenter.permission.DefaultPermissionChecker;
import com.demo.authcenter.permission.PermissionChecker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Starter：方法级安全能力自动装配（统一入口）。
 */
@AutoConfiguration
@Conditional(AuthCenterMethodSecurityMode.AnyEnabledCondition.class)
public class AuthCenterMethodSecurityAutoConfiguration {

    /**
     * 子模块 1：Spring Security 方法级注解能力
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EnableMethodSecurity.class)
    @Conditional(AuthCenterMethodSecurityMode.SpringEnabledCondition.class)
    @EnableMethodSecurity(prePostEnabled = true)
    static class SpringMethodSecurityConfiguration {
        // 只需要启用注解能力即可，无需额外 Bean
    }

    /**
     * 子模块 2：@RequirePerm AOP 能力
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RequirePermAspect.class)
    @Conditional(AuthCenterMethodSecurityMode.RequirePermEnabledCondition.class)
    static class RequirePermConfiguration {

        /**
         * PermissionChecker：允许业务方覆盖
         */
        @Bean
        @ConditionalOnMissingBean
        public PermissionChecker permissionChecker() {
            return new DefaultPermissionChecker();
        }

        /**
         * 开启 @RequirePerm 的 AOP 拦截：允许业务方覆盖
         */
        @Bean
        @ConditionalOnMissingBean
        public RequirePermAspect requirePermAspect(PermissionChecker permissionChecker) {
            return new RequirePermAspect(permissionChecker);
        }
    }
}
