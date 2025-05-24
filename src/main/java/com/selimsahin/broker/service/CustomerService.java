package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Customer;
import com.selimsahin.broker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
