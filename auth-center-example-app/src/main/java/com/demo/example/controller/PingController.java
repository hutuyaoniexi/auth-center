package com.demo.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
