/**
 * Web 入口过滤器：认证（Authentication）阶段。
 *
 * <p>本包负责在请求进入业务 Controller 之前完成认证信息建立，典型职责：</p>
 * <ul>
 *   <li>从请求提取 token（Authorization: Bearer ...）</li>
 *   <li>校验 token 合法性（签名/过期/issuer/aud/typ 等）</li>
 *   <li>结合 TokenStore 实现“可主动失效”的能力（登出/踢人/刷新链路作废）</li>
 *   <li>将认证结果写入 SecurityContext，供后续授权阶段使用</li>
 * </ul>
 *
 * <p>注意：</p>
 * <ul>
 *   <li>Filter 只负责认证，不负责具体业务授权（授权由 @PreAuthorize / @RequirePerm 等完成）</li>
 *   <li>认证失败时不直接输出响应，而交由 AuthenticationEntryPoint 统一返回 JSON</li>
 * </ul>
 */
package com.demo.authcenter.filter;
