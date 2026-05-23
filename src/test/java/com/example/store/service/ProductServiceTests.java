package com.example.store.service;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.dto.response.ProductSummaryResponse;
import com.example.store.entity.Product;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.ProductMapper;
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
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateProductRequest();
        request.setDescription("Keyboard");
    }

    @Test
    void createProductShouldReturnCreatedProduct() {
        Product saved = new Product();
        saved.setId(1L);
        saved.setDescription("Keyboard");

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setDescription("Keyboard");

        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productMapper.productToProductResponse(saved)).thenReturn(response);

        ProductResponse actual = productService.createProduct(request);
        assertEquals(1L, actual.getId());
        assertEquals("Keyboard", actual.getDescription());
    }

    @Test
    void getProductByIdShouldReturnProductWhenExists() {
        Product product = new Product();
        product.setId(10L);
        product.setDescription("Mouse");

        ProductResponse response = new ProductResponse();
        response.setId(10L);
        response.setDescription("Mouse");

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductResponse(product)).thenReturn(response);

        ProductResponse actual = productService.getProductById(10L);
        assertEquals(10L, actual.getId());
    }

    @Test
    void getProductByIdShouldThrowNotFoundWhenMissing() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(100L));
    }

    @Test
    void getAllProductsShouldReturnPagedSummaries() {
        Pageable pageable = PageRequest.of(0, 20);
        Product product = new Product();
        product.setId(1L);
        product.setDescription("Keyboard");

        ProductSummaryResponse summaryResponse = new ProductSummaryResponse();
        summaryResponse.setId(1L);
        summaryResponse.setDescription("Keyboard");

        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product), pageable, 1));
        when(productMapper.productToProductSummaryResponse(product)).thenReturn(summaryResponse);

        var actual = productService.getAllProducts(pageable);
        assertEquals(1, actual.getTotalElements());
        assertEquals("Keyboard", actual.getContent().get(0).getDescription());
    }
}
