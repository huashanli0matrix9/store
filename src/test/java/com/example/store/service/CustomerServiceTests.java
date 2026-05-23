package com.example.store.service;

import com.example.store.dto.response.CustomerResponse;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getCustomersShouldSearchByQueryWhenProvided() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        CustomerResponse response = new CustomerResponse();
        response.setId(1L);
        response.setName("John Doe");

        when(customerRepository.findByNameContainingIgnoreCase("john")).thenReturn(List.of(customer));
        when(customerMapper.customersToCustomerResponses(List.of(customer))).thenReturn(List.of(response));

        List<CustomerResponse> actual = customerService.getCustomers(" john ");
        assertEquals(1, actual.size());
        assertEquals("John Doe", actual.get(0).getName());
    }
}
