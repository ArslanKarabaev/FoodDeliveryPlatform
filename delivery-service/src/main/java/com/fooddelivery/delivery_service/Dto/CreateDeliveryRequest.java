package com.fooddelivery.delivery_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateDeliveryRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID restaurantId;

    @NotNull
    private UUID clientId;

    private String restaurantName;
    private String restaurantPhone;

    @NotBlank
    private String fromAddress;
    private Double fromLat;
    private Double fromLng;

    private String clientName;
    private String clientPhone;

    @NotBlank
    private String toAddress;
    private Double toLat;
    private Double toLng;

    private String orderAmount;
    private Integer orderItemsCount;
    private String orderDescription;
}
