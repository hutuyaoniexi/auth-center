/**
 * 自动装配（Spring Boot Starter 核心入口）。
 *
 * <p>本包负责将鉴权模块能力以“开箱即用”的方式接入业务系统，主要包括：</p>
 * <ul>
 *   <li>注册核心 Bean（如 JwtUtil、TokenStore 默认实现、Filter 等）</li>
 *   <li>提供可覆盖扩展点：业务方可通过自定义 @Bean 覆盖默认实现</li>
 *   <li>提供开关与条件装配：根据 classpath / 配置决定是否启用某些能力</li>
 * </ul>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>Starter 提供默认可运行实现（方便 demo/本地运行）</li>
 *   <li>生产环境可替换：如 TokenStore 可替换为 Redis/JDBC 实现</li>
 *   <li>职责清晰：装配只做“注册与开关”，业务逻辑在对应模块内实现</li>
 * </ul>
 */
package com.demo.authcenter.autoconfig;
