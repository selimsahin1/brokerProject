package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Asset;
import com.selimsahin.broker.model.Order;
import com.selimsahin.broker.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;

    public List<Order> getOrders(String customerId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDateTime, endDateTime);
    }

    public Order createOrder(String customerId, String assetName, Order.OrderSide side, int size, double price) throws IllegalAccessException {
        Asset asset;

        if (side == Order.OrderSide.BUY) {
            asset = assetService.getOrCreateAsset(customerId, "TRY");

            double totalCost = size * price;
            if (asset.getUsableSize() < totalCost) {
                throw new IllegalAccessException("Insufficient TRY balance");
            }
            asset.setUsableSize((int) (asset.getUsableSize() - totalCost));
        } else {
            asset = assetService.getOrCreateAsset(customerId, assetName);
            if (asset.getUsableSize() < size) {
                throw new IllegalAccessException("Insufficient asset quantity to sell");
            }
            asset.setUsableSize(asset.getUsableSize() - size);
        }

        assetService.updateAsset(asset);

        Order order = Order.builder()
                .customerId(customerId)
                .assetName(assetName)
                .side(side)
                .size(size)
                .price(price)
                .status(Order.OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    public void cancelOrderWithPermission(Long orderId, String activeUsername, boolean admin) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!admin && !order.getCustomerId().equals(activeUsername)) {
            throw new AccessDeniedException("You cannot cancel someone else's order");
        }

        cancelOrder(orderId); // mevcut logiği çağır
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be canceled");
        }

        Asset asset;
        if (order.getSide() == Order.OrderSide.BUY) {
            asset = assetService.getOrCreateAsset(order.getCustomerId(), "TRY");
            asset.setUsableSize((int) (asset.getUsableSize() + order.getSize() * order.getPrice()));

        } else {
            asset = assetService.getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
        }

        assetService.updateAsset(asset);
        order.setStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public Order matchOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be matched");
        }

        Asset asset;

        if (order.getSide() == Order.OrderSide.BUY) {
            asset = assetService.getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            asset.setSize(asset.getSize() + order.getSize());
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
        } else {
            Asset tryAsset = assetService.getOrCreateAsset(order.getCustomerId(), "TRY");
            double income = order.getSize() * order.getPrice();
            tryAsset.setSize(tryAsset.getSize() + (int) income);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + (int) income);
            asset = assetService.getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            asset.setSize(asset.getSize() - order.getSize());
        }
        assetService.updateAsset(asset);
        order.setStatus(Order.OrderStatus.MATCHED);
        return orderRepository.save(order);
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(Order.OrderStatus.PENDING);
    }
}
