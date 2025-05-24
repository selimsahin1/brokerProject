package com.selimsahin.broker.controller;

import com.selimsahin.broker.dto.OrderRequest;
import com.selimsahin.broker.model.Order;
import com.selimsahin.broker.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) throws IllegalAccessException {

        return orderService.createOrder(request.getCustomerId(),
                request.getAssetName(), request.getSide(),
                request.getSize(), request.getPrice());
    }

    @GetMapping
    public List<Order> listOrders(@RequestParam(required = false) String customerId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {


        return orderService.getOrders(customerId, start, end);
    }

    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Long orderId) throws AccessDeniedException {
        // Yetkilendirme kontrolü orderService içinde yapılabilir, güvenli olması için
        orderService.cancelOrder(orderId);
    }

    @PostMapping("/{orderId}/match")
    public Order matchOrder(@PathVariable Long orderId) throws AccessDeniedException {
        return orderService.matchOrder(orderId);
    }

    @GetMapping("/pending")
    public List<Order> listPendingOrders() throws AccessDeniedException {
        return orderService.getPendingOrders();
    }
}