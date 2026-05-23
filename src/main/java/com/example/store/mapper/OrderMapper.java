package com.example.store.mapper;

import com.example.store.dto.response.OrderCustomerResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.dto.response.OrderSummaryResponse;
import com.example.store.dto.response.ProductSummaryResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponse orderToOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setDescription(order.getDescription());
        response.setCustomer(toOrderCustomerResponse(order.getCustomer()));
        response.setProducts(toProductSummaryResponses(order.getProducts()));
        return response;
    }

    public List<OrderResponse> ordersToOrderResponses(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::orderToOrderResponse).collect(Collectors.toList());
    }

    public OrderSummaryResponse orderToOrderSummaryResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setId(order.getId());
        response.setDescription(order.getDescription());
        return response;
    }

    private OrderCustomerResponse toOrderCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        OrderCustomerResponse response = new OrderCustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        return response;
    }

    private List<ProductSummaryResponse> toProductSummaryResponses(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream().map(this::toProductSummaryResponse).collect(Collectors.toList());
    }

    private ProductSummaryResponse toProductSummaryResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.setId(product.getId());
        response.setDescription(product.getDescription());
        return response;
    }
}
