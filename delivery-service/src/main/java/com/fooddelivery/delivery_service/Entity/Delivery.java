package com.fooddelivery.delivery_service.Entity;

import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.query.hql.spi.DotIdentifierConsumer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private UUID restaurantId;

    private UUID clientId;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    //otkuda
    private String fromAddress;
    private Double fromLat;
    private Double fromLng;

    //kuda
    private String toAddress;
    private Double toLat;
    private Double toLng;

    private String yandexDeliveryId;
    private String yandexTrackingUrl;

    private BigDecimal deliveryFee;
    private Integer estimatedMinutes;

    //fallback
    private boolean manualMode = false;
    private String courierName;
    private String courierPhone;

    private String cancelledReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
