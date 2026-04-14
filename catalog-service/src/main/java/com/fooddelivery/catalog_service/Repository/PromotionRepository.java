package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.Promotion;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    List<Promotion> findByRestaurantIdAndIsActiveIsTrue(UUID restaurantId);

    @Query("SELECT p from Promotion p WHERE p.isActive = true AND (p.endsAt IS NULL OR p.endsAt > :now)")
    List<Promotion> findAllActive(@Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT p.restaurantId FROM Promotion p WHERE p.isActive = true " +
            "AND (p.endsAt IS NULL OR p.endsAt > :now)")
    List<UUID> findRestaurantIdsWithActivePromotions(@Param("now") LocalDateTime now);

    boolean existsByMenuItemIdAndIsActiveTrue(UUID menuItemId);

}
