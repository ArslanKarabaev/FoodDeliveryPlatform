package com.fooddelivery.catalog_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private UUID menuItemId;

    @Column(nullable = false)
    private Integer discountPercent;

    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private BigDecimal  discountedPrice;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
