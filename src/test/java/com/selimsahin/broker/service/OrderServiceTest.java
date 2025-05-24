package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Asset;
import com.selimsahin.broker.model.Order;
import com.selimsahin.broker.model.Order.OrderSide;
import com.selimsahin.broker.model.Order.OrderStatus;
import com.selimsahin.broker.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBuyOrder_success() throws IllegalAccessException {
        // Arrange
        String customerId = "alice";
        String assetName = "ASELS";
        int size = 10;
        double price = 5.0;
        double totalCost = size * price;

        Asset tryAsset = Asset.builder()
                .customerId(customerId)
                .assetName("TRY")
                .size(1000)
                .usableSize((int) totalCost + 10)
                .build();

        when(assetService.getOrCreateAsset(customerId, "TRY")).thenReturn(tryAsset);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order result = orderService.createOrder(customerId, assetName, OrderSide.BUY, size, price);

        // Assert
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetService).updateAsset(any());
        verify(orderRepository).save(any());
    }

    @Test
    void testCreateBuyOrder_insufficientTRY_throwsException() {
        String customerId = "alice";
        String assetName = "ASELS";
        int size = 10;
        double price = 5.0;

        Asset tryAsset = Asset.builder()
                .customerId(customerId)
                .assetName("TRY")
                .usableSize(20) // 10 * 5 = 50 > 20 → yetersiz
                .build();

        when(assetService.getOrCreateAsset(customerId, "TRY")).thenReturn(tryAsset);

        IllegalAccessException ex = assertThrows(IllegalAccessException.class, () ->
                orderService.createOrder(customerId, assetName, OrderSide.BUY, size, price)
        );

        assertEquals("Insufficient TRY balance", ex.getMessage());
    }

    @Test
    void testCreateSellOrder_success() throws IllegalAccessException {
        String customerId = "bob";
        String assetName = "THYAO";
        int size = 5;
        double price = 10.0;

        Asset stockAsset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .usableSize(size + 5)
                .build();

        when(assetService.getOrCreateAsset(customerId, assetName)).thenReturn(stockAsset);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(customerId, assetName, OrderSide.SELL, size, price);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetService).updateAsset(any());
        verify(orderRepository).save(any());
    }

    @Test
    void testCreateSellOrder_insufficientAsset_throwsException() {
        String customerId = "bob";
        String assetName = "THYAO";
        int size = 10;

        Asset stockAsset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .usableSize(5) // yetersiz
                .build();

        when(assetService.getOrCreateAsset(customerId, assetName)).thenReturn(stockAsset);

        IllegalAccessException ex = assertThrows(IllegalAccessException.class, () ->
                orderService.createOrder(customerId, assetName, OrderSide.SELL, size, 10.0)
        );

        assertEquals("Insufficient asset quantity to sell", ex.getMessage());
    }

    @Test
    void testCancelOrder_success() {
        String customerId = "alice";
        Order order = Order.builder()
                .id(1L)
                .customerId(customerId)
                .assetName("ASELS")
                .side(OrderSide.SELL)
                .price(10.0)
                .size(5)
                .status(OrderStatus.PENDING)
                .build();

        Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName("ASELS")
                .usableSize(0)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetService.getOrCreateAsset(customerId, "ASELS")).thenReturn(asset);

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(assetService).updateAsset(asset);
    }

    @Test
    void testCancelOrder_nonPending_throwsException() {
        Order order = Order.builder()
                .id(2L)
                .customerId("alice")
                .status(OrderStatus.MATCHED)
                .build();

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                orderService.cancelOrder(2L)
        );

        assertEquals("Only PENDING orders can be canceled", ex.getMessage());
    }

    @Test
    void testCancelOrder_notFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                orderService.cancelOrder(99L)
        );

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void testMatchOrder_buyOrder_success() {
        // Arrange
        String customerId = "alice";
        Order order = Order.builder()
                .id(1L)
                .customerId(customerId)
                .assetName("ASELS")
                .side(OrderSide.BUY)
                .price(5.0)
                .size(10)
                .status(OrderStatus.PENDING)
                .build();

        Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName("ASELS")
                .size(20)
                .usableSize(15)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetService.getOrCreateAsset(customerId, "ASELS")).thenReturn(asset);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order result = orderService.matchOrder(1L);

        // Assert
        assertEquals(OrderStatus.MATCHED, result.getStatus());
        assertEquals(30, asset.getSize()); // 20 + 10
        assertEquals(25, asset.getUsableSize()); // 15 + 10
        verify(assetService).updateAsset(asset);
    }

    @Test
    void testGetOrders_filtersByDateRange() {
        List<Order> orders = List.of(
                Order.builder().id(1L).build(),
                Order.builder().id(2L).build()
        );

        when(orderRepository.findByCustomerIdAndCreateDateBetween(anyString(), any(), any()))
                .thenReturn(orders);

        var result = orderService.getOrders("alice",
                LocalDate.now().minusDays(5),
                LocalDate.now());

        assertEquals(2, result.size());
    }

    @Test
    void testGetPendingOrders_returnsOnlyPending() {
        List<Order> orders = List.of(
                Order.builder().id(1L).status(OrderStatus.PENDING).build()
        );

        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

        var result = orderService.getPendingOrders();

        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void testCancelOrderWithPermission_byOwner_success() {
        String customerId = "alice";
        Order order = Order.builder()
                .id(3L)
                .customerId(customerId)
                .assetName("ASELS")
                .side(OrderSide.SELL)
                .price(10.0)
                .size(5)
                .status(OrderStatus.PENDING)
                .build();

        Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName("ASELS")
                .usableSize(10)
                .build();

        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        when(assetService.getOrCreateAsset(customerId, "ASELS")).thenReturn(asset);

        orderService.cancelOrderWithPermission(3L, customerId, false);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(assetService).updateAsset(asset);
    }

    @Test
    void testCancelOrderWithPermission_byAnotherUser_shouldThrow() {
        Order order = Order.builder()
                .id(4L)
                .customerId("alice")
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(4L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () ->
                orderService.cancelOrderWithPermission(4L, "bob", false)
        );
    }

}