package com.example.store.service;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerResponse;
import com.example.store.dto.response.CustomerSummaryResponse;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public Page<CustomerSummaryResponse> getAllCustomers(Pageable pageable) {
        return getCustomers(null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CustomerSummaryResponse> getCustomers(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return customerRepository.findAll(pageable).map(customerMapper::customerToCustomerSummaryResponse);
        }
        return customerRepository
                .findByNameContainingIgnoreCase(query.trim(), pageable)
                .map(customerMapper::customerToCustomerSummaryResponse);
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        Customer saved = customerRepository.save(customer);
        return customerMapper.customerToCustomerResponse(saved);
    }
}
