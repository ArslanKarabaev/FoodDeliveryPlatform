package com.fooddelivery.catalog_service.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "Название обязательно")
    private String name;
    private Integer position;
}