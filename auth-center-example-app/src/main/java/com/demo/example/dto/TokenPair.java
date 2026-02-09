package com.demo.example.dto;

/**
 * token对象
 * @param accessToken
 * @param refreshToken
 */
public record TokenPair(String accessToken, String refreshToken) {}
