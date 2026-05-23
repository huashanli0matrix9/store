package com.example.store.controller;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.exception.NotFoundException;
import com.example.store.service.ProductService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponse productResponse;
    private CreateProductRequest createProductRequest;

    @BeforeEach
    void setUp() {
        createProductRequest = new CreateProductRequest();
        createProductRequest.setDescription("Keyboard");

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setDescription("Keyboard");
        productResponse.setOrderIds(List.of(100L));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Keyboard"))
                .andExpect(jsonPath("$.orderIds[0]").value(100));
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Keyboard"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Keyboard"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new NotFoundException("Product not found: 99"));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found: 99"))
                .andExpect(jsonPath("$.path").value("/products/99"));
    }
}
