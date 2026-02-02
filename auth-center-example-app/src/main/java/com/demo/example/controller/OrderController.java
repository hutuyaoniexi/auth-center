package com.demo.example.controller;

import com.demo.example.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 对应 Spring 官方注解
    @GetMapping("/{id}")
    public String getOrder1(@PathVariable Long id) {
        return orderService.getOrderBySpring(id);
    }

    // 对应 starter 自定义注解
    @GetMapping("/{id}/v2")
    public String getOrder2(@PathVariable Long id) {
        return orderService.getOrderByStarter(id);
    }
}
