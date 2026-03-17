package com.fooddelivery.delivery_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDeliveryRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID restaurantId;

    @NotNull
    private UUID clientId;

    @NotBlank
    private String fromAddress;
    private Double fromLat;
    private Double fromLng;

    @NotBlank
    private String toAddress;
    private Double toLat;
    private Double toLng;
}
