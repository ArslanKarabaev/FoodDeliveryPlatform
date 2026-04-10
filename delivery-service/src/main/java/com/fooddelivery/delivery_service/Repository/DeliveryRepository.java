package com.fooddelivery.delivery_service.Repository;

import com.fooddelivery.delivery_service.Entity.Delivery;
import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID orderId);
    Optional<Delivery> findByYandexDeliveryId(String yandexDeliveryId);
    List<Delivery> findByStatusInAndManualModeFalse(List<DeliveryStatus> statuses);
}
