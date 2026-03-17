package com.fooddelivery.delivery_service.Dto;

import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private UUID id;
    private UUID orderId;
    private DeliveryStatus status;
    private String yandexTrackingUrl;
    private BigDecimal deliveryFee;
    private Integer estimatedMinutes;
    private boolean manualMode;
    private String courierName;
    private String courierPhone;
    private LocalDateTime createdAt;
}
