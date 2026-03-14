package com.fooddelivery.catalog_service.Entity;

import com.fooddelivery.catalog_service.Enum.CuisineType;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(length = 2000)
    private String description;

    private String address;
    private String city;

    private Double latitude;
    private Double longitude;

    private String logoUrl;
    private String coverUrl;

    @Enumerated(EnumType.STRING)
    private CuisineType cuisineType;

    private String phone;
    private String email;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> workingHours;

    private BigDecimal minOrderAmount;
    private Double deliveryZoneRadiusKm;

    private boolean isActive = false;
    private boolean isVerified = false;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> paymentDetails;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, String>> certificates;

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate = BigDecimal.valueOf(12.00);

    private UUID cafeAdminId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
