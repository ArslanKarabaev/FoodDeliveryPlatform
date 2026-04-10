package com.fooddelivery.catalog_service.Repository;

import com.fooddelivery.catalog_service.Entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByClientIdAndRestaurantId(UUID clientId, UUID restaurantId);

    List<Review> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);

    Integer countByRestaurantId(UUID restaurantId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurantId = :restaurantId")
    Double calculateAverageRating(@Param("restaurantId") UUID restaurantId);


}
