package com.example.store.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String description;
    private List<Long> orderIds;
}
