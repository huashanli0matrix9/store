package com.example.store.mapper;

import com.example.store.dto.response.CustomerOrderResponse;
import com.example.store.dto.response.CustomerResponse;
import com.example.store.dto.response.CustomerSummaryResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    public CustomerResponse customerToCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setOrders(toCustomerOrderResponses(customer.getOrders()));
        return response;
    }

    public List<CustomerResponse> customersToCustomerResponses(List<Customer> customers) {
        if (customers == null) {
            return Collections.emptyList();
        }
        return customers.stream().map(this::customerToCustomerResponse).collect(Collectors.toList());
    }

    public CustomerSummaryResponse customerToCustomerSummaryResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        return response;
    }

    private List<CustomerOrderResponse> toCustomerOrderResponses(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::toCustomerOrderResponse).collect(Collectors.toList());
    }

    private CustomerOrderResponse toCustomerOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        CustomerOrderResponse response = new CustomerOrderResponse();
        response.setId(order.getId());
        response.setDescription(order.getDescription());
        return response;
    }
}
