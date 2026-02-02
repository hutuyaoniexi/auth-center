package com.demo.authcenter.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePerm {

    /**
     * 权限枚举类型，例如 OrderPerm.class
     */
    Class<? extends Enum<?>> perm();

    /**
     * 枚举常量名，例如 {"READ","UPDATE"}
     */
    String[] actions();

    /**
     * true=AND（需要同时具备所有权限），false=OR（具备任意一个即可）
     */
    boolean requireAll() default false;
}
