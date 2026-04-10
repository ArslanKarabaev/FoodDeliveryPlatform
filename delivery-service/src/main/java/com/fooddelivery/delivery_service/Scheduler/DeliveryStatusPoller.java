package com.fooddelivery.delivery_service.Scheduler;

import com.fooddelivery.delivery_service.Client.YandexDeliveryClient;
import com.fooddelivery.delivery_service.Entity.Delivery;
import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import com.fooddelivery.delivery_service.Repository.DeliveryRepository;
import com.fooddelivery.delivery_service.Service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusPoller {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;
    private final YandexDeliveryClient yandexClient;

    @Scheduled(fixedDelay = 30_000)
    public void pollActiveDeliveries() {
        List<DeliveryStatus> activeStatuses = List.of(
                DeliveryStatus.CONFIRMED,
                DeliveryStatus.SEARCHING_COURIER,
                DeliveryStatus.COURIER_ASSIGNED,
                DeliveryStatus.COURIER_AT_RESTAURANT,
                DeliveryStatus.COURIER_AT_CLIENT
        );

        List<Delivery> active = deliveryRepository
                .findByStatusInAndManualModeFalse(activeStatuses);

        for (Delivery delivery : active) {
            try {
                String yandexStatus = yandexClient
                        .getClaimStatus(delivery.getYandexDeliveryId());
                deliveryService
                        .handleYandexWebhook(delivery.getYandexDeliveryId(), yandexStatus);
            } catch (Exception e) {
                log.error("Ошибка polling: {}", e.getMessage());
            }
        }
    }
}