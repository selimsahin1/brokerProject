package com.selimsahin.broker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;

    private String assetName;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    private int size;

    private double price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createDate;

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderStatus {
        PENDING, MATCHED, CANCELED
    }
}
