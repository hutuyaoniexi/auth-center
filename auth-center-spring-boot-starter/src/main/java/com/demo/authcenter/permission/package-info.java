/**
 * 权限校验与权限点建模。
 *
 * <p>本包用于承载授权（Authorization）阶段的可扩展能力：</p>
 * <ul>
 *   <li>PermissionChecker：权限判定扩展点（业务可覆盖默认实现）</li>
 *   <li>（可选）PermissionAction/PermissionCode：权限点接口，由 enum 实现，避免硬编码字符串</li>
 * </ul>
 *
 * <p>说明：</p>
 * <ul>
 *   <li>roles（ROLE_）通常由 Spring Security 直接基于 GrantedAuthority 判断</li>
 *   <li>perms（权限点）通常由 PermissionChecker 进行策略判定（RBAC/ABAC/自定义映射）</li>
 * </ul>
 */
package com.demo.authcenter.permission;
