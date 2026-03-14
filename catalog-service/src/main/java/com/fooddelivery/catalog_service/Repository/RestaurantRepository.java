package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Optional<Restaurant> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);
    Page<Restaurant> findByIsActiveTrueAndCity(String city, Pageable pageable);
    Page<Restaurant> findByIsActiveTrueAndCuisineType(CuisineType cuisineType, Pageable pageable);
}
