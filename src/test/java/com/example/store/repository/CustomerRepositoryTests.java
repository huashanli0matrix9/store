package com.example.store.repository;

import com.example.store.config.JpaAuditingConfig;
import com.example.store.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(JpaAuditingConfig.class)
class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByNameContainingIgnoreCaseShouldSearchBySubstringCaseInsensitive() {
        Customer c1 = new Customer();
        c1.setName("John Doe");
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setName("Alice Smith");
        customerRepository.save(c2);

        var page = customerRepository.findByNameContainingIgnoreCase("joHn", PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("John Doe", page.getContent().get(0).getName());
    }
}
