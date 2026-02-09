/**
 * 注解能力实现：AOP 拦截与授权校验。
 *
 * <p>本包通过 Aspect 拦截业务方法上的注解（如 @RequirePerm），并完成授权判断：</p>
 * <ul>
 *   <li>解析注解配置（roles/perms/mode）</li>
 *   <li>基于 SecurityContext 获取当前认证信息</li>
 *   <li>调用 PermissionChecker 执行权限点判定</li>
 * </ul>
 *
 * <p>授权失败通常抛出 AccessDeniedException，并由 AccessDeniedHandler 输出统一 JSON（403）。</p>
 */
package com.demo.authcenter.aop;
