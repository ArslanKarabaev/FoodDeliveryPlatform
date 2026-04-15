package com.fooddelivery.order_service.Dto;

import com.fooddelivery.order_service.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID clientId;
    private UUID restaurantId;
    private OrderStatus status;
    private List<Map<String, Object>> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal platformCommission;
    private BigDecimal totalAmount;
    private Map<String, Object> deliveryAddress;
    private String yandexTrackingUrl;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime createdAt;
    private boolean includeCutlery;
    private LocalDateTime readyAt;
}
