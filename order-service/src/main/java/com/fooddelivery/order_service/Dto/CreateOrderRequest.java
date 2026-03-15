package com.fooddelivery.order_service.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Id ресторана обязателен")
    private UUID restaurantId;

    @NotEmpty(message = "Список позиций не может быть пустым")
    private List<OrderItemRequest> items;

    @NotNull(message = "Адрес доставки обязателен")
    private DeliveryAddressRequest deliveryAddress;
}
