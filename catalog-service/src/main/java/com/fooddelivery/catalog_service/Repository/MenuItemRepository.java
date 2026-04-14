package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.MenuItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByCategoryId(UUID categoryId);

    List<MenuItem> findByCategoryRestaurantId(UUID restaurantId);

    @Query("SELECT m FROM MenuItem m JOIN FETCH m.category c JOIN FETCH c.restaurant WHERE m.id = :id")
    Optional<MenuItem> findByIdWithCategoryAndRestaurant(@Param("id") UUID id);
}