package com.fooddelivery.catalog_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateMenuItemRequest {
    @NotNull(message = "Категория обязательна")
    private UUID categoryId;

    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    private BigDecimal price;

    private Integer weightGrams;
    private List<String> allergens;
    private List<String> tags;
}