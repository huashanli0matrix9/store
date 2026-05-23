package com.example.store.service;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.dto.response.OrderSummaryResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.exception.BadRequestException;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::orderToOrderSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return orderMapper.orderToOrderResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCustomerId()));
        List<Product> products = productRepository.findAllById(request.getProductIds());
        validateAllRequestedProductsExist(request.getProductIds(), products);

        Order order = new Order();
        order.setDescription(request.getDescription());
        order.setCustomer(customer);
        order.setProducts(products);

        Order saved = orderRepository.save(order);
        return orderMapper.orderToOrderResponse(saved);
    }

    private void validateAllRequestedProductsExist(List<Long> requestedProductIds, List<Product> products) {
        Set<Long> foundIds = new HashSet<>();
        for (Product product : products) {
            foundIds.add(product.getId());
        }

        List<Long> missingIds = requestedProductIds.stream()
                .filter(id -> !foundIds.contains(id))
                .distinct()
                .toList();

        if (!missingIds.isEmpty()) {
            throw new BadRequestException("Products not found: " + missingIds);
        }
    }
}
