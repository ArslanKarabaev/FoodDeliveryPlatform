package com.fooddelivery.order_service.Repository;

import com.fooddelivery.order_service.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);
    Optional<Order> findByOrderNumber(String orderNumber);
}
