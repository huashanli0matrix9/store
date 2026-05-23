package com.example.store.service;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerSummaryResponse;
import com.example.store.dto.response.CustomerResponse;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomerShouldPersistAndReturnResponse() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John Doe");

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("John Doe");

        CustomerResponse response = new CustomerResponse();
        response.setId(1L);
        response.setName("John Doe");

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);
        when(customerMapper.customerToCustomerResponse(saved)).thenReturn(response);

        CustomerResponse actual = customerService.createCustomer(request);
        assertEquals(1L, actual.getId());
        assertEquals("John Doe", actual.getName());
    }

    @Test
    void getCustomersShouldSearchByQueryWhenProvided() {
        Pageable pageable = PageRequest.of(0, 20);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setId(1L);
        response.setName("John Doe");

        when(customerRepository.findByNameContainingIgnoreCase("john", pageable))
                .thenReturn(new PageImpl<>(List.of(customer), pageable, 1));
        when(customerMapper.customerToCustomerSummaryResponse(customer)).thenReturn(response);

        var actual = customerService.getCustomers(" john ", pageable);
        assertEquals(1, actual.getTotalElements());
        assertEquals("John Doe", actual.getContent().get(0).getName());
    }

    @Test
    void getCustomersShouldReturnAllWhenQueryBlank() {
        Pageable pageable = PageRequest.of(0, 20);
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setName("Jane Doe");

        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setId(2L);
        response.setName("Jane Doe");

        when(customerRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(customer), pageable, 1));
        when(customerMapper.customerToCustomerSummaryResponse(customer)).thenReturn(response);

        var actual = customerService.getCustomers("   ", pageable);
        assertEquals(1, actual.getTotalElements());
        assertEquals("Jane Doe", actual.getContent().get(0).getName());
    }
}
