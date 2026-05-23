package com.example.store.repository;

import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase
class AuditingPersistenceTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPopulateAuditAndVersionFieldsAfterPersist() {
        Customer customer = new Customer();
        customer.setName("Audit Customer");
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        Product product = new Product();
        product.setDescription("Audit Product");
        Product savedProduct = productRepository.saveAndFlush(product);

        Order order = new Order();
        order.setDescription("Audit Order");
        order.setCustomer(savedCustomer);
        order.setProducts(List.of(savedProduct));
        Order savedOrder = orderRepository.saveAndFlush(order);

        assertNotNull(savedCustomer.getCreatedAt());
        assertNotNull(savedCustomer.getUpdatedAt());
        assertNotNull(savedCustomer.getVersion());

        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
        assertNotNull(savedProduct.getVersion());

        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getUpdatedAt());
        assertNotNull(savedOrder.getVersion());
    }
}
