package com.fooddelivery.order_service.Dto;

import lombok.Data;

@Data
public class DeliveryAddressRequest {
    private String street;
    private String city;
    private String apartment;
    private Double lat;
    private Double lng;
    private String comment;
}
