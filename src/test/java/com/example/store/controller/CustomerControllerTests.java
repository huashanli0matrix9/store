package com.example.store.controller;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerResponse;
import com.example.store.dto.response.CustomerSummaryResponse;
import com.example.store.service.CustomerService;
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

@WebMvcTest(CustomerController.class)
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerResponse customerResponse;
    private CustomerSummaryResponse customerSummaryResponse;
    private CreateCustomerRequest createCustomerRequest;

    @BeforeEach
    void setUp() {
        createCustomerRequest = createCustomerRequest("John Doe");
        customerResponse = customerResponse(1L, "John Doe");
        customerSummaryResponse = customerSummaryResponse(1L, "John Doe");
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(customerResponse);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldReturnPaginatedCustomers() throws Exception {
        when(customerService.getCustomers(any(), any()))
                .thenReturn(new PageImpl<>(List.of(customerSummaryResponse), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }

    @Test
    void shouldReturnPaginatedCustomersByQuery() throws Exception {
        when(customerService.getCustomers(any(), any()))
                .thenReturn(new PageImpl<>(List.of(customerSummaryResponse), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/customer").param("query", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerNameBlank() throws Exception {
        CreateCustomerRequest invalidRequest = createCustomerRequest("   ");

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/customer"))
                .andExpect(jsonPath("$.details.name").exists());
    }

    private CreateCustomerRequest createCustomerRequest(String name) {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName(name);
        return request;
    }

    private CustomerResponse customerResponse(Long id, String name) {
        CustomerResponse response = new CustomerResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }

    private CustomerSummaryResponse customerSummaryResponse(Long id, String name) {
        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }
}
