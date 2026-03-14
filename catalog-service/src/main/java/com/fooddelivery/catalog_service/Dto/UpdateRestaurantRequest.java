package com.fooddelivery.catalog_service.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class UpdateRestaurantRequest {
    private String description;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private BigDecimal minOrderAmount;
    private Double deliveryZoneRadiusKm;
    private Map<String, String> workingHours;
}