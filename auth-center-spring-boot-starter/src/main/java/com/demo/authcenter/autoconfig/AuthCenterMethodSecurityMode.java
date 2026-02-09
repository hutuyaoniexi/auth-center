package com.demo.authcenter.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 方法级安全模式定义与条件判断（Starter 内部使用）。
 *
 * <h3>配置项</h3>
 * <pre>
 * auth-center.method-security-mode = NONE | SPRING | REQUIRE_PERM | BOTH
 * </pre>
 *
 * <h3>语义说明</h3>
 * <ul>
 *   <li><b>NONE</b>：关闭所有方法级能力（默认，零侵入）</li>
 *   <li><b>SPRING</b>：仅启用 Spring Security 方法级注解（@PreAuthorize 等）</li>
 *   <li><b>REQUIRE_PERM</b>：仅启用自定义 @RequirePerm AOP</li>
 *   <li><b>BOTH</b>：两者都启用</li>
 * </ul>
 *
 * <h3>容错策略</h3>
 * <ul>
 *   <li>未配置 / 空值：视为 {@code NONE}</li>
 *   <li>非法值：降级为 {@code NONE}（不炸启动），并输出 warn 日志</li>
 *   <li>支持 {@code require-perm} / {@code require_perm} 等写法（会统一归一化）</li>
 * </ul>
 */
public final class AuthCenterMethodSecurityMode {

    /** 配置键*/
    public static final String KEY = "auth-center.method-security-mode";

    private static final Logger log =
            LoggerFactory.getLogger(AuthCenterMethodSecurityMode.class);

    private AuthCenterMethodSecurityMode() {}

    /**
     * 方法级安全模式枚举。
     */
    public enum Mode {
        NONE,
        SPRING,
        REQUIRE_PERM,
        BOTH;

        /**
         * 从 {@link Environment} 解析方法级安全模式。
         */
        public static Mode from(Environment env) {
            String v = env.getProperty(KEY);
            if (v == null || v.isBlank()) {
                return NONE; // 默认零侵入
            }

            String normalized = v.trim()
                    .toUpperCase()
                    .replace('-', '_');

            try {
                return Mode.valueOf(normalized);
            } catch (IllegalArgumentException ex) {
                log.warn(
                        "Invalid config: {}='{}'. Fallback to NONE. Allowed: NONE, SPRING, REQUIRE_PERM, BOTH",
                        KEY, v
                );
                return NONE;
            }
        }

        /** 是否启用任意方法级能力 */
        public boolean anyEnabled() {
            return this != NONE;
        }

        /** 是否启用 Spring Security 方法级注解 */
        public boolean springEnabled() {
            return this == SPRING || this == BOTH;
        }

        /** 是否启用 @RequirePerm AOP */
        public boolean requirePermEnabled() {
            return this == REQUIRE_PERM || this == BOTH;
        }
    }

    /** auth-center.method-security-mode != NONE */
    public static final class AnyEnabledCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Mode.from(context.getEnvironment()).anyEnabled();
        }
    }

    /** auth-center.method-security-mode == SPRING || BOTH */
    public static final class SpringEnabledCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Mode.from(context.getEnvironment()).springEnabled();
        }
    }

    /** auth-center.method-security-mode == REQUIRE_PERM || BOTH */
    public static final class RequirePermEnabledCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Mode.from(context.getEnvironment()).requirePermEnabled();
        }
    }
}
