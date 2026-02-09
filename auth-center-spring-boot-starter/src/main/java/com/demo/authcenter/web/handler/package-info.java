/**
 * Spring Security Web Handler：
 * <ul>
 *   <li>RestAuthenticationEntryPoint：认证失败时返回 401 JSON</li>
 *   <li>RestAccessDeniedHandler：授权失败时返回 403 JSON</li>
 * </ul>
 *
 * <p>职责边界：</p>
 * <ul>
 *   <li>401：我是谁？token 是否可用？（未认证/认证失败）</li>
 *   <li>403：你是谁我知道，但你没权限（已认证/授权失败）</li>
 * </ul>
 */
package com.demo.authcenter.web.handler;
