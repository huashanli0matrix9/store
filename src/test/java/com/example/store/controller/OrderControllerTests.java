package com.example.store.controller;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderCustomerResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.exception.NotFoundException;
import com.example.store.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse orderResponse;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setDescription("Test Order");
        createOrderRequest.setCustomerId(1L);

        OrderCustomerResponse customer = new OrderCustomerResponse();
        customer.setId(1L);
        customer.setName("John Doe");

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setDescription("Test Order");
        orderResponse.setCustomer(customer);
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"));
    }

    @Test
    void testGetOrder() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..description").value("Test Order"))
                .andExpect(jsonPath("$..customer.name").value("John Doe"));
    }

    @Test
    void testCreateOrderWithInvalidRequestShouldReturnBadRequest() throws Exception {
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        invalidRequest.setDescription(" ");
        invalidRequest.setCustomerId(null);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/order"))
                .andExpect(jsonPath("$.details.description").exists())
                .andExpect(jsonPath("$.details.customerId").exists());
    }

    @Test
    void testCreateOrderWithMissingCustomerShouldReturnNotFound() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenThrow(new NotFoundException("Customer not found: 1"));

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Customer not found: 1"))
                .andExpect(jsonPath("$.path").value("/order"));
    }
}
