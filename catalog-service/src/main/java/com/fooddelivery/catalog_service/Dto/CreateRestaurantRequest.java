package com.fooddelivery.catalog_service.Dto;

import com.fooddelivery.catalog_service.Enum.CuisineType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateRestaurantRequest {
    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private CuisineType cuisineType;
    private String phone;
    private String email;
    private BigDecimal minOrderAmount;
    private Double deliveryZoneRadiusKm;
    private UUID cafeAdminId;
}
