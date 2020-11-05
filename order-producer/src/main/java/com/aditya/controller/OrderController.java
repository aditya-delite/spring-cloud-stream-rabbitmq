package com.aditya.controller;

import com.aditya.dto.OrderDto;
import com.aditya.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto) {
        service.persist(orderDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/order/{oId}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable("oId") String orderId) {
        OrderDto order = service.getOrderDetails(orderId);
        return ResponseEntity.ok(order);
    }

}
