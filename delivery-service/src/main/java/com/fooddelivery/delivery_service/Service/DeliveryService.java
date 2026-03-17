package com.fooddelivery.delivery_service.Service;

import com.fooddelivery.delivery_service.Dto.CreateDeliveryRequest;
import com.fooddelivery.delivery_service.Dto.DeliveryResponse;
import com.fooddelivery.delivery_service.Dto.ManualUpdateRequest;
import com.fooddelivery.delivery_service.Entity.Delivery;
import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import com.fooddelivery.delivery_service.Repository.DeliveryRepository;
import com.sun.jdi.event.StepEvent;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${yandex.delivery.api.url}")
    private String yandexApiUrl;

    @Value("${yandex.delivery.api.key}")
    private String yandexApiKey;

    public DeliveryResponse getQuote(CreateDeliveryRequest request){
        //TODO заменить заглушки
        log.info("Получение расчёта доставки для заказа: {}", request.getOrderId());

        return DeliveryResponse.builder()
                .orderId(request.getOrderId())
                .status(DeliveryStatus.ESTIMATING)
                .deliveryFee(BigDecimal.valueOf(100))
                .estimatedMinutes(30)
                .build();
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request){
        if(deliveryRepository.findByOrderId(request.getOrderId()).isPresent()){
            throw new RuntimeException("Доставка для этого заказа уже существует");
        }

        //TODO заменить заглушки
        log.info("Создание доставки для заказа: {}", request.getOrderId());

        String fakeYandexId= "YD-" + request.getOrderId().toString().substring(0, 8);
        String fakeTrackingUrl = "https://go.yandex/tracking/" + fakeYandexId;

        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .restaurantId(request.getRestaurantId())
                .clientId(request.getClientId())
                .status(DeliveryStatus.CONFIRMED)
                .fromAddress(request.getFromAddress())
                .fromLat(request.getFromLat())
                .fromLng(request.getFromLng())
                .toAddress(request.getToAddress())
                .toLat(request.getToLat())
                .toLng(request.getToLng())
                .yandexDeliveryId(fakeYandexId)
                .yandexTrackingUrl(fakeTrackingUrl)
                .deliveryFee(BigDecimal.valueOf(100))
                .estimatedMinutes(30)
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        rabbitTemplate.convertAndSend("delivery.created",
                saved.getOrderId().toString() + ":" + saved.getClientId().toString());

        return toResponse(saved);
    }

    public DeliveryResponse getDeliveryStatus(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));
        return toResponse(delivery);
    }

    public void handleYandexWebhook(String yandexDeliveryId, String status){
        //TODO заменить заглушки
        log.info("Webhook от Яндекса: {} - {}", yandexDeliveryId, status);

        Delivery delivery = deliveryRepository.findByYandexDeliveryId(yandexDeliveryId)
                .orElseThrow(()-> new RuntimeException("Доставка не найдена"));

        DeliveryStatus newStatus = mapYandexStatus(status);
        delivery.setStatus(newStatus);
        deliveryRepository.save(delivery);

        rabbitTemplate.convertAndSend("delivery.status.updated",
                delivery.getOrderId().toString() + ":" + newStatus.name());
    }

    public void manualUpdate(UUID orderId, ManualUpdateRequest request) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));

        delivery.setStatus(request.getStatus());
        delivery.setManualMode(true);
        if (request.getCourierName() != null) delivery.setCourierName(request.getCourierName());
        if (request.getCourierPhone() != null) delivery.setCourierPhone(request.getCourierPhone());

        deliveryRepository.save(delivery);

        rabbitTemplate.convertAndSend("delivery.status.updated",
                orderId.toString() + ":" + request.getStatus().name());
    }


    private DeliveryStatus mapYandexStatus(String yandexStatus) {
        return switch (yandexStatus) {
            case "accepted" -> DeliveryStatus.CONFIRMED;
            case "performer_found" -> DeliveryStatus.COURIER_ASSIGNED;
            case "performer_arrived" -> DeliveryStatus.COURIER_ARRIVED;
            case "pickup_arrived" -> DeliveryStatus.DELIVERING;
            case "delivered" -> DeliveryStatus.DELIVERED;
            case "cancelled" -> DeliveryStatus.CANCELLED;
            default -> DeliveryStatus.PENDING;
        };
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .yandexTrackingUrl(delivery.getYandexTrackingUrl())
                .deliveryFee(delivery.getDeliveryFee())
                .estimatedMinutes(delivery.getEstimatedMinutes())
                .manualMode(delivery.isManualMode())
                .courierName(delivery.getCourierName())
                .courierPhone(delivery.getCourierPhone())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
}
