/**
 * 对业务方暴露的注解（API 面）。
 *
 * <p>例如：</p>
 * <ul>
 *   <li>@RequirePerm：声明方法/类需要的权限点与角色要求</li>
 * </ul>
 *
 * <p>注解本身仅表达“需求”，具体拦截与校验由 aop 包实现。</p>
 */
package com.demo.authcenter.annotation;
