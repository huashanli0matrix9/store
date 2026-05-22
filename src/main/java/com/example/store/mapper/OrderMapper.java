package com.example.store.mapper;

import com.example.store.dto.response.OrderCustomerResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;

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
        return response;
    }

    public List<OrderResponse> ordersToOrderResponses(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::orderToOrderResponse).collect(Collectors.toList());
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
}
