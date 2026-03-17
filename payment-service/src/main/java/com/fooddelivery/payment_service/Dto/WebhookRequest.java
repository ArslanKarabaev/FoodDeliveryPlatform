package com.fooddelivery.payment_service.Dto;

import lombok.Data;

import java.util.Map;

@Data
public class WebhookRequest {
    private String providerPaymentId;
    private String status;
    private String signature;
    private Map<String, Object> data;
}
