package com.example.store.mapper;

import com.example.store.dto.response.ProductResponse;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    public ProductResponse productToProductResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setDescription(product.getDescription());
        response.setOrderIds(toOrderIds(product.getOrders()));
        return response;
    }

    public List<ProductResponse> productsToProductResponses(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream().map(this::productToProductResponse).collect(Collectors.toList());
    }

    private List<Long> toOrderIds(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(Order::getId).collect(Collectors.toList());
    }
}
