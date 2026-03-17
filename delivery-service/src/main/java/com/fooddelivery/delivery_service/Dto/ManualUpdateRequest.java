package com.fooddelivery.delivery_service.Dto;

import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManualUpdateRequest {
    @NotNull
    private DeliveryStatus status;
    private String courierName;
    private String courierPhone;
}
