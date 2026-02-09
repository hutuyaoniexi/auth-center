/**
 * 配置属性（ConfigurationProperties）与模块开关。
 *
 * <p>本包用于承载 starter 对外暴露的配置“契约”，例如：</p>
 * <ul>
 *   <li>JWT 参数：密钥、issuer、audience、过期时间、header 等</li>
 *   <li>模块开关：是否启用 filter/aop、忽略路径 ignorePaths 等</li>
 * </ul>
 *
 * <p>建议约定：</p>
 * <ul>
 *   <li>Properties 类只放在该包，避免散落在 security/model 等位置导致歧义</li>
 *   <li>Properties 命名尽量体现层级：AuthCenterProperties / JwtProps</li>
 * </ul>
 */
package com.demo.authcenter.properties;
