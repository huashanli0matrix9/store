package com.example.store.dto.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String description;
    private Long customerId;
}
