package com.selimsahin.broker.repository;

import com.selimsahin.broker.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByCustomerIdAndCreateDateBetween(String customerId, LocalDateTime start, LocalDateTime end);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByCustomerIdAndStatus(String customerId, Order.OrderStatus status);
}