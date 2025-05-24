package com.selimsahin.broker.dto;

import com.selimsahin.broker.model.Order;
import lombok.Getter;

@Getter
public class OrderRequest {
    private String customerId;
    private String assetName;
    private Order.OrderSide side;
    private int size;
    private double price;
}
