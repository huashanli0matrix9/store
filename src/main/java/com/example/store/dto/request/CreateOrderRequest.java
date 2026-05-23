package com.example.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "description must not be blank")
    @Size(max = 255, message = "description must be at most 255 characters")
    private String description;

    @NotNull(message = "customerId must not be null")
    private Long customerId;

    @NotEmpty(message = "productIds must not be empty")
    @Size(max = 100, message = "productIds must contain at most 100 items")
    private List<Long> productIds;
}
