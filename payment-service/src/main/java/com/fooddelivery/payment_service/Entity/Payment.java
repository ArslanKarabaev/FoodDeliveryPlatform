package com.fooddelivery.payment_service.Entity;

import com.fooddelivery.payment_service.Enum.PaymentStatus;
import com.fooddelivery.payment_service.Enum.PayoutStatus;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID restaurantId;

    private UUID clientId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal restaurantPayout;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String provider = "FREEDOM_PAY";
    private String providerPaymentId;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> providerResponse;

    @Enumerated(EnumType.STRING)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    private String paymentUrl;
    private LocalDateTime payoutAt;
    private LocalDateTime refundedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
