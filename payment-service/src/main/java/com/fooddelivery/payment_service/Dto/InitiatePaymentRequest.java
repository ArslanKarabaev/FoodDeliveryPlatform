package com.fooddelivery.payment_service.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InitiatePaymentRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID restaurantId;

    @NotNull
    private UUID clientId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal platformFee;

    @NotNull
    private BigDecimal restaurantPayout;

}
