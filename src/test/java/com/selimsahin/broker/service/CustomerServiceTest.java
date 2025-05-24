package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Customer;
import com.selimsahin.broker.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsername_found() {
        Customer mockCustomer = Customer.builder()
                .id(1L)
                .username("alice")
                .password("alice123")
                .isAdmin(false)
                .build();

        when(customerRepository.findByUsername("alice"))
                .thenReturn(Optional.of(mockCustomer));

        Optional<Customer> result = customerService.findByUsername("alice");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void testFindByUsername_notFound() {
        when(customerRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.findByUsername("ghost");

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveCustomer_success() {
        Customer input = Customer.builder()
                .username("bob")
                .password("bob123")
                .isAdmin(false)
                .build();

        Customer saved = Customer.builder()
                .id(2L)
                .username("bob")
                .password("bob123")
                .isAdmin(false)
                .build();

        when(customerRepository.save(input)).thenReturn(saved);

        Customer result = customerService.save(input);

        assertEquals(2L, result.getId());
        assertEquals("bob", result.getUsername());
    }
}