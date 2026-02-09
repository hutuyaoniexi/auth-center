package com.demo.authcenter.annotation;

import java.lang.annotation.*;

/**
 * 声明接口/类所需权限（由 RequirePermAspect 拦截执行）。
 *
 * <p>推荐写法：perms 直接写最终权限字符串（如 "order:read"）。
 * <p>兼容写法：perm + actions（枚举常量名 -> PermissionAction.value() -> "order:read"）。
 *
 * <p>组合策略：
 * - ALL：roles 与 perms（若配置了）都要满足
 * - ANY：roles / perms 任一满足即可（某一类为空则忽略该类）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface RequirePerm {

    /** 推荐：直接声明权限点字符串，例如 {"order:read","order:write"} */
    String[] perms() default {};

    /**
     * 兼容：权限枚举类（枚举需实现 PermissionAction）
     * @deprecated 优先使用 perms()
     */
    @Deprecated
    Class<? extends Enum<?>> perm() default Dummy.class;

    /**
     * 兼容：权限动作（枚举常量名），如 {"READ","WRITE"}
     * @deprecated 优先使用 perms()
     */
    @Deprecated
    String[] actions() default {};

    /** 允许的角色，支持 "ADMIN" 或 "ROLE_ADMIN" */
    String[] roles() default {};

    /** 组合策略：ALL=roles 与 perms 都满足；ANY=满足其一即可 */
    Mode mode() default Mode.ALL;

    enum Mode { ALL, ANY }

    /** 仅用于给 perm() 提供默认值 */
    enum Dummy { NONE }
}
