package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByCategoryId(UUID categoryId);
    List<MenuItem> findByCategoryRestaurantId(UUID restaurantId);
}