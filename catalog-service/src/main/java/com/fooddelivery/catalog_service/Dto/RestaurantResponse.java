package com.fooddelivery.catalog_service.Dto;

import com.fooddelivery.catalog_service.Enum.CuisineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String logoUrl;
    private String coverUrl;
    private CuisineType cuisineType;
    private String phone;
    private String email;
    private Map<String, String> workingHours;
    private BigDecimal minOrderAmount;
    private Double deliveryZoneRadiusKm;
    private BigDecimal commissionRate;
}