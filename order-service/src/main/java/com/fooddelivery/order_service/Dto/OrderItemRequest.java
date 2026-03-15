package com.fooddelivery.order_service.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemRequest {
    @NotNull
    private UUID itemId;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @Min(1)
    private int quantity;
}