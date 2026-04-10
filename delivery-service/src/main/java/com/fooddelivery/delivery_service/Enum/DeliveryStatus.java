package com.fooddelivery.delivery_service.Enum;

public enum DeliveryStatus {
    PENDING,
    ESTIMATING,
    READY_FOR_APPROVAL,
    CONFIRMED,
    SEARCHING_COURIER,
    COURIER_ASSIGNED,
    COURIER_AT_RESTAURANT,
    COURIER_AT_CLIENT,
    DELIVERING,
    DELIVERED,
    RETURNING,
    CANCELLED,
    CANCELLED_WITH_FEE,
    FAILED
}