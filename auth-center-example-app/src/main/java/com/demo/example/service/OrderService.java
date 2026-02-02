package com.demo.example.service;

import com.demo.authcenter.annotation.RequirePerm;
import com.demo.example.permission.OrderPerm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    // ✅ Spring 官方方式
    @PreAuthorize(
            "hasAuthority(T(com.demo.example.permission.OrderPerm).READ.value())"
    )
    public String getOrderBySpring(Long id) {
        return "order-" + id;
    }

    // ✅ 你 starter 提供的方式
    @RequirePerm(
            perm = OrderPerm.class,
            actions = {"READ"}
    )
    public String getOrderByStarter(Long id) {
        return "order-" + id;
    }
}
