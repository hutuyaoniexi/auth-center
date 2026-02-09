package com.demo.authcenter.permission;

/**
 * 兼容旧写法用的权限映射接口。
 *
 * <p>当 @RequirePerm 使用 perm + actions 时，枚举需实现本接口，
 * 通过 value() 将枚举常量映射为最终权限字符串（如 "order:read"）。</p>
 *
 * <p>推荐新写法直接使用 @RequirePerm(perms = {...})，无需实现本接口。</p>
 */
public interface PermissionAction {

    /** 返回权限字符串（最终写入 Authority / 用于权限判断）。 */
    String value();
}
