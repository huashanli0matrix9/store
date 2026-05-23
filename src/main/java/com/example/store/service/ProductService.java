package com.example.store.service;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.dto.response.ProductSummaryResponse;
import com.example.store.entity.Product;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setDescription(request.getDescription());
        Product saved = productRepository.save(product);
        return productMapper.productToProductResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::productToProductSummaryResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return productMapper.productToProductResponse(product);
    }
}
