package com.demo.authcenter.security.dto;

/**
 * Starter 内部返回的 Token 对（access + refresh）。
 *
 * <p>用于登录/刷新等流程在 Starter 内部传递结果；业务系统如需自定义响应结构，
 * 可将本对象映射为自己的 DTO。</p>
 */
public record JwtTokenPair(String accessToken, String refreshToken) {}
