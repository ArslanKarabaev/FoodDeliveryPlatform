package com.fooddelivery.catalog_service.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreatePromotionRequest {

    @NotNull
    private UUID menuItemId;

    @NotNull
    @Min(1) @Max(99)
    private Integer discountPercent;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
