/**
 * 安全模型（Security Model）。
 *
 * <p>用于承载鉴权流程中的轻量模型对象，例如：</p>
 * <ul>
 *   <li>JwtTokenPair：access/refresh token 对</li>
 *   <li>其他与 JWT claims 或安全上下文相关的只读模型</li>
 * </ul>
 *
 * <p>说明：</p>
 * <ul>
 *   <li>本包不放配置属性（Properties），避免与 config 包语义冲突</li>
 * </ul>
 */
package com.demo.authcenter.security.dto;
