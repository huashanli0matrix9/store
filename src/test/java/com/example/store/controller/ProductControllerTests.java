package com.example.store.controller;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.dto.response.ProductSummaryResponse;
import com.example.store.exception.NotFoundException;
import com.example.store.service.ProductService;
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
    private ProductSummaryResponse productSummaryResponse;
    private CreateProductRequest createProductRequest;

    @BeforeEach
    void setUp() {
        createProductRequest = createProductRequest("Keyboard");
        productResponse = productResponse(1L, "Keyboard", List.of(100L));
        productSummaryResponse = productSummaryResponse(1L, "Keyboard");
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Keyboard"))
                .andExpect(jsonPath("$.orderIds[0]").value(100));
    }

    @Test
    void shouldReturnPaginatedProducts() throws Exception {
        when(productService.getAllProducts(any()))
                .thenReturn(new PageImpl<>(List.of(productSummaryResponse), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Keyboard"));
    }

    @Test
    void shouldGetProductByIdSuccessfully() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Keyboard"));
    }

    @Test
    void shouldReturnNotFoundWhenProductMissing() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new NotFoundException("Product not found: 99"));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found: 99"))
                .andExpect(jsonPath("$.path").value("/products/99"));
    }

    @Test
    void shouldReturnBadRequestWhenProductDescriptionBlank() throws Exception {
        CreateProductRequest invalidRequest = createProductRequest(" ");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.details.description").exists());
    }

    private CreateProductRequest createProductRequest(String description) {
        CreateProductRequest request = new CreateProductRequest();
        request.setDescription(description);
        return request;
    }

    private ProductResponse productResponse(Long id, String description, List<Long> orderIds) {
        ProductResponse response = new ProductResponse();
        response.setId(id);
        response.setDescription(description);
        response.setOrderIds(orderIds);
        return response;
    }

    private ProductSummaryResponse productSummaryResponse(Long id, String description) {
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.setId(id);
        response.setDescription(description);
        return response;
    }
}
