package com.example.store.repository;

import com.example.store.config.JpaAuditingConfig;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(JpaAuditingConfig.class)
class OrderProductRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistOrderProductManyToManyRelationAndInitializeVersion() {
        Customer customer = new Customer();
        customer.setName("Relation Customer");
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        Product p1 = new Product();
        p1.setDescription("Keyboard");
        Product p2 = new Product();
        p2.setDescription("Mouse");
        productRepository.saveAllAndFlush(List.of(p1, p2));

        Order order = new Order();
        order.setDescription("Order with products");
        order.setCustomer(savedCustomer);
        order.setProducts(List.of(p1, p2));
        Order savedOrder = orderRepository.saveAndFlush(order);

        var found = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertEquals(2, found.getProducts().size());
        assertNotNull(found.getVersion());
        assertTrue(found.getVersion() >= 0);
    }
}
