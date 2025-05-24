package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Customer;
import com.selimsahin.broker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String role = customer.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
        return new User(customer.getUsername(), customer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
