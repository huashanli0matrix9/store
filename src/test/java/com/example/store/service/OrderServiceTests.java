package com.example.store.service;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateOrderRequest();
        request.setDescription("Test Order");
        request.setCustomerId(999L);
    }

    @Test
    void createOrderShouldThrowNotFoundWhenCustomerDoesNotExist() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void getOrderByIdShouldReturnOrderWhenExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        Order order = new Order();
        order.setId(10L);
        order.setDescription("Sample");
        order.setCustomer(customer);

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
    void getOrderByIdShouldThrowNotFoundWhenMissing() {
        when(orderRepository.findById(111L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderById(111L));
    }
}
