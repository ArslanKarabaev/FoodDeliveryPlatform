package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Optional<Restaurant> findBySlug(String slug);
    Optional<Restaurant> findByCafeAdminId(UUID cafeAdminId);
    boolean existsBySlug(String slug);
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true " +
            "AND (:city IS NULL OR r.city = :city) " +
            "AND (:cuisineType IS NULL OR r.cuisineType = :cuisineType) " +
            "AND (:minRating IS NULL OR r.averageRating >= :minRating) " +
            "AND (:maxDeliveryTime IS NULL OR r.estimatedDeliveryMinutes <= :maxDeliveryTime)")
    Page<Restaurant> findWithFilters(
            @Param("city") String city,
            @Param("cuisineType") CuisineType cuisineType,
            @Param("minRating") Double minRating,
            @Param("maxDeliveryTime") Integer maxDeliveryTime,
            Pageable pageable);

    List<Restaurant> findTop10ByIsActiveTrueOrderByAverageRatingDesc();

    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true " +
            "AND r.cuisineType IN :cuisines " +
            "AND r.id NOT IN :excludeIds " +
            "ORDER BY r.averageRating DESC")
    List<Restaurant> findRecommended(
            @Param("cuisines") List<CuisineType> cuisines,
            @Param("excludeIds") List<UUID> excludeIds,
            Pageable pageable);
}
