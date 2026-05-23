package com.example.store.controller;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderCustomerResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.dto.response.OrderSummaryResponse;
import com.example.store.dto.response.ProductSummaryResponse;
import com.example.store.exception.BadRequestException;
import com.example.store.exception.NotFoundException;
import com.example.store.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private OrderSummaryResponse orderSummaryResponse;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setDescription("Test Order");
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setProductIds(List.of(10L, 11L));

        OrderCustomerResponse customer = new OrderCustomerResponse();
        customer.setId(1L);
        customer.setName("John Doe");

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setDescription("Test Order");
        orderResponse.setCustomer(customer);
        ProductSummaryResponse p1 = new ProductSummaryResponse();
        p1.setId(10L);
        p1.setDescription("Keyboard");
        ProductSummaryResponse p2 = new ProductSummaryResponse();
        p2.setId(11L);
        p2.setDescription("Mouse");
        orderResponse.setProducts(List.of(p1, p2));

        orderSummaryResponse = new OrderSummaryResponse();
        orderSummaryResponse.setId(1L);
        orderSummaryResponse.setDescription("Test Order");
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.products[0].description").value("Keyboard"));
    }

    @Test
    void testGetOrder() throws Exception {
        when(orderService.getAllOrders(any()))
                .thenReturn(new PageImpl<>(List.of(orderSummaryResponse), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Test Order"));
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.products[1].description").value("Mouse"));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new NotFoundException("Order not found: 99"));

        mockMvc.perform(get("/order/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Order not found: 99"))
                .andExpect(jsonPath("$.path").value("/order/99"));
    }

    @Test
    void testCreateOrderWithInvalidRequestShouldReturnBadRequest() throws Exception {
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        invalidRequest.setDescription(" ");
        invalidRequest.setCustomerId(null);
        invalidRequest.setProductIds(List.of());

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/order"))
                .andExpect(jsonPath("$.details.description").exists())
                .andExpect(jsonPath("$.details.customerId").exists())
                .andExpect(jsonPath("$.details.productIds").exists());
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

    @Test
    void testCreateOrderWithInvalidProductsShouldReturnBadRequest() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new BadRequestException("Products not found: [999]"));

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Products not found: [999]"))
                .andExpect(jsonPath("$.path").value("/order"));
    }
}
