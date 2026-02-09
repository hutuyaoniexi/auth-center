/**
 * 业务侧扩展点（SPI）。
 *
 * <p>Starter 不关心业务的用户体系与权限来源，本包定义业务方需要实现的接口：</p>
 * <ul>
 *   <li>AuthUser：统一的用户信息模型（userId/authorities 等）</li>
 *   <li>AuthUserService：根据 userId 加载用户信息（可对接 DB/LDAP/第三方）</li>
 * </ul>
 *
 * <p>通过 SPI，starter 实现“即插即用 + 可扩展”，避免与具体业务强耦合。</p>
 */
package com.demo.authcenter.spi;
