package com.fooddelivery.delivery_service.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public class YandexClientDto {
    @Data
    @Builder
    public static class ClaimRequest {
        private List<ClaimItem> items;
        private List<RoutePoint> route_points;
        private EmergencyContact emergency_contact;
        private ClientRequirements client_requirements;
        private String comment;
    }

    @Data
    @Builder
    public static class ClaimItem {
        private String title;
        private Integer pickup_point;
        private Integer droppof_point; //опечатка должна быть
        private Integer quantity;
        private ItemSize size;
        private Double weight;
        private String cost_value;
        private String cost_currency;
    }

    @Data
    @Builder
    public static class ItemSize {
        private Double length;
        private Double width;
        private Double height;
    }

    @Data
    @Builder
    public static class RoutePoint {
        private Integer point_id;
        private String type;           // "source" или "destination"
        private Integer visit_order;
        private PointAddress address;
        private PointContact contact;
        private Boolean skip_confirmation;
    }

    @Data
    @Builder
    public static class PointAddress {
        private List<Double> coordinates;
        private String fullname;
    }

    @Data
    @Builder
    public static class PointContact {
        private String name;
        private String phone;
    }

    @Data
    @Builder
    public static class EmergencyContact {
        private String name;
        private String phone;
    }

    @Data
    @Builder
    public static class ClientRequirements {
        private String taxi_class;  // "express"
    }

    @Data
    public static class ClaimResponse {
        private String id;
        private String status;
        private Integer version;
        private Integer eta;
        private Pricing pricing;
    }

    @Data
    public static class Pricing {
        private PricingOffer offer;
        private String currency;
        private String final_price;
    }

    @Data
    public static class PricingOffer {
        private String price;
        private String valid_until;
    }

}
