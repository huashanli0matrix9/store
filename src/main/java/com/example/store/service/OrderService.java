package com.example.store.service;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderMapper.ordersToOrderResponses(orderRepository.findAll());
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCustomerId()));

        Order order = new Order();
        order.setDescription(request.getDescription());
        order.setCustomer(customer);

        Order saved = orderRepository.save(order);
        return orderMapper.orderToOrderResponse(saved);
    }
}
