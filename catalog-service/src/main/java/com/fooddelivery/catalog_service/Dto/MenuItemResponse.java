package com.fooddelivery.catalog_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean isAvailable;
    private Integer weightGrams;
    private List<String> allergens;
    private List<String> tags;
}