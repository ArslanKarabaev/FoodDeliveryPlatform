package com.fooddelivery.order_service.Repository;

import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime expiredBefore);
    @Query("SELECT o.restaurantId, COUNT(o) as cnt FROM Order o " +
            "WHERE o.clientId = :clientId AND o.status = 'DELIVERED' " +
            "GROUP BY o.restaurantId ORDER BY cnt DESC")
    List<UUID> findTopRestaurantIdsByClientId(@Param("clientId") UUID clientId, Pageable pageable);

    Page<Order> findByRestaurantIdAndStatusOrderByCreatedAtDesc(
            UUID restaurantId, OrderStatus status, Pageable pageable);

    Page<Order> findByRestaurantIdOrderByCreatedAtDesc(
            UUID restaurantId, Pageable pageable);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.restaurantId = :restaurantId GROUP BY o.status")
    List<Object[]> countByRestaurantIdGroupByStatus(@Param("restaurantId") UUID restaurantId);

}
