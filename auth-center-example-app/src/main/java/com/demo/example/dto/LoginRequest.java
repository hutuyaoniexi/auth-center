package com.demo.example.dto;

/**
 * 登录请求 DTO
 * @param username
 * @param password
 */
public record LoginRequest(String username, String password) {}
