package com.selimsahin.broker.controller;

import com.selimsahin.broker.jwt.JwtUtil;
import com.selimsahin.broker.model.Customer;
import com.selimsahin.broker.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        return customerService.findByUsername(username)
                .filter(customer -> passwordEncoder.matches(password, customer.getPassword()))
                .map(customer -> ResponseEntity.ok(jwtUtil.generateToken(customer.getUsername(), customer.isAdmin())))
                .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }
}
