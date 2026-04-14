package com.fooddelivery.catalog_service.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateReviewRequest {
    @NotNull
    private UUID restaurantId;

    @NotNull
    private UUID orderId;

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 1000)
    private String comment;
    private UUID menuItemId;
}
