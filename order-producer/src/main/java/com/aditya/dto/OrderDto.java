package com.aditya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {
    private String orderId;
    private String itemName;
    private int quantity;
    private double orderAmount;
    private String status;
}

