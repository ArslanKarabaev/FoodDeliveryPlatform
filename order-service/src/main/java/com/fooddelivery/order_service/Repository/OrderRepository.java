package com.fooddelivery.order_service.Repository;

import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime expiredBefore);
}
