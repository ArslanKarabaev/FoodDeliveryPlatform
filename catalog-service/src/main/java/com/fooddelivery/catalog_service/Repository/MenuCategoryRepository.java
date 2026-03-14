package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    List<MenuCategory> findByRestaurantIdOrderByPosition(UUID restaurantId);
}