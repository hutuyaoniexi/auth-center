package com.demo.authcenter.autoconfig;

import com.demo.authcenter.aop.RequirePermAspect;
import com.demo.authcenter.permission.DefaultPermissionChecker;
import com.demo.authcenter.permission.PermissionChecker;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RequirePermAspect.class)
public class AuthCenterMethodSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PermissionChecker permissionChecker() {
        return new DefaultPermissionChecker();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "authcenter.method",
            name = "require-perm",
            havingValue = "true",
            matchIfMissing = true
    )
    public RequirePermAspect requirePermAspect(PermissionChecker checker) {
        return new RequirePermAspect(checker);
    }
}
