/**
 * 异常与错误码定义。
 *
 * <p>本包用于承载鉴权模块的“错误语义层”，例如：</p>
 * <ul>
 *   <li>AuthErrorCodes：错误码常量（401xx/403xx）</li>
 *   <li>（可选）AuthException 体系：TokenInvalidException/PermissionDeniedException 等</li>
 * </ul>
 *
 * <p>说明：</p>
 * <ul>
 *   <li>HTTP 状态码表达协议语义（401/403），业务错误码用于更细粒度区分原因</li>
 * </ul>
 */
package com.demo.authcenter.exception;
