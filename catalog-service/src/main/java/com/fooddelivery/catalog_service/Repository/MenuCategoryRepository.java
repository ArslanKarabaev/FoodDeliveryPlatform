package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.MenuCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    List<MenuCategory> findByRestaurantIdOrderByPosition(UUID restaurantId);

    @Query("SELECT c FROM MenuCategory c LEFT JOIN FETCH c.items WHERE c.restaurant.id = :restaurantId ORDER BY c.position")
    List<MenuCategory> findByRestaurantIdWithItems(@Param("restaurantId") UUID restaurantId);
}