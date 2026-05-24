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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        request = createOrderRequest("Test Order", 999L, List.of(1L, 2L));
    }

    @Test
    void shouldThrowNotFoundWhenCustomerDoesNotExistOnCreateOrder() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void shouldReturnOrderWhenGetByIdExists() {
        Customer customer = customer(1L, "John Doe");

        Order order = order(10L, "Sample", customer, List.of());

        OrderResponse response = new OrderResponse();
        response.setId(10L);
        response.setDescription("Sample");

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderResponse(order)).thenReturn(response);

        OrderResponse actual = orderService.getOrderById(10L);
        assertEquals(10L, actual.getId());
        assertEquals("Sample", actual.getDescription());
    }

    @Test
    void shouldThrowNotFoundWhenGetOrderByIdMissing() {
        when(orderRepository.findById(111L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderById(111L));
    }

    @Test
    void shouldCreateOrderWhenCustomerAndProductsValid() {
        Customer customer = customer(999L, null);
        Product p1 = product(1L, null);
        Product p2 = product(2L, null);

        Order savedOrder = order(200L, "Test Order", customer, List.of(p1, p2));

        OrderResponse response = new OrderResponse();
        response.setId(200L);
        response.setDescription("Test Order");

        when(customerRepository.findById(999L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(p1, p2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.orderToOrderResponse(savedOrder)).thenReturn(response);

        OrderResponse actual = orderService.createOrder(request);
        assertEquals(200L, actual.getId());
    }

    @Test
    void shouldThrowBadRequestWhenAnyProductMissingOnCreateOrder() {
        Customer customer = customer(999L, null);
        Product p1 = product(1L, null);

        when(customerRepository.findById(999L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(p1));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    void shouldReturnPagedOrderSummaries() {
        Pageable pageable = PageRequest.of(0, 20);
        Order order = new Order();
        order.setId(1L);
        order.setDescription("Summary Order");

        OrderSummaryResponse summaryResponse = new OrderSummaryResponse();
        summaryResponse.setId(1L);
        summaryResponse.setDescription("Summary Order");

        when(orderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(order), pageable, 1));
        when(orderMapper.orderToOrderSummaryResponse(order)).thenReturn(summaryResponse);

        var actual = orderService.getAllOrders(pageable);
        assertEquals(1, actual.getTotalElements());
        assertEquals("Summary Order", actual.getContent().get(0).getDescription());
    }

    private CreateOrderRequest createOrderRequest(String description, Long customerId, List<Long> productIds) {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setDescription(description);
        createOrderRequest.setCustomerId(customerId);
        createOrderRequest.setProductIds(productIds);
        return createOrderRequest;
    }

    private Customer customer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }

    private Product product(Long id, String description) {
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        return product;
    }

    private Order order(Long id, String description, Customer customer, List<Product> products) {
        Order order = new Order();
        order.setId(id);
        order.setDescription(description);
        order.setCustomer(customer);
        order.setProducts(products);
        return order;
    }
}
