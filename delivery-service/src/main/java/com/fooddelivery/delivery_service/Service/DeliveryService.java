package com.fooddelivery.delivery_service.Service;

import com.fooddelivery.delivery_service.Client.YandexDeliveryClient;
import com.fooddelivery.delivery_service.Dto.CreateDeliveryRequest;
import com.fooddelivery.delivery_service.Dto.DeliveryResponse;
import com.fooddelivery.delivery_service.Dto.ManualUpdateRequest;
import com.fooddelivery.delivery_service.Dto.YandexClientDto;
import com.fooddelivery.delivery_service.Entity.Delivery;
import com.fooddelivery.delivery_service.Enum.DeliveryStatus;
import com.fooddelivery.delivery_service.Repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final YandexDeliveryClient yandexClient;

    public DeliveryResponse getQuote(CreateDeliveryRequest request) {
        //TODO заменить заглушки
        log.info("Получение расчёта доставки для заказа: {}", request.getOrderId());

        return DeliveryResponse.builder()
                .orderId(request.getOrderId())
                .status(DeliveryStatus.ESTIMATING)
                .deliveryFee(BigDecimal.valueOf(100))
                .estimatedMinutes(30)
                .build();
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        if (deliveryRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Доставка для этого заказа уже существует");
        }

        log.info("Создание доставки для заказа: {}", request.getOrderId());

        // Строим запрос к Яндексу
        YandexClientDto.ClaimRequest claimRequest =
                YandexClientDto.ClaimRequest.builder()
                        .items(List.of(YandexClientDto.ClaimItem.builder()
                                .title(request.getOrderDescription())      // ← реальное описание
                                .pickup_point(1)
                                .droppof_point(2)
                                .quantity(request.getOrderItemsCount())    // ← реальное количество
                                .weight(request.getOrderItemsCount() * 0.5) // ← примерно 0.5 кг на позицию
                                .size(YandexClientDto.ItemSize.builder()
                                        .length(0.3).width(0.3).height(0.2).build())
                                .cost_value(request.getOrderAmount())      // ← реальная сумма заказа
                                .cost_currency("KGS")
                                .build()))
                        .route_points(List.of(
                                YandexClientDto.RoutePoint.builder()
                                        .point_id(1)
                                        .type("source")
                                        .visit_order(1)
                                        .address(YandexClientDto.PointAddress.builder()
                                                .coordinates(List.of(request.getFromLng(), request.getFromLat()))
                                                .fullname(request.getFromAddress())
                                                .build())
                                        .contact(YandexClientDto.PointContact.builder()
                                                .name(request.getRestaurantName())
                                                .phone(request.getRestaurantPhone())
                                                .build())
                                        .skip_confirmation(true)
                                        .build(),
                                YandexClientDto.RoutePoint.builder()
                                        .point_id(2)
                                        .type("destination")
                                        .visit_order(2)
                                        .address(YandexClientDto.PointAddress.builder()
                                                .coordinates(List.of(request.getToLng(), request.getToLat()))
                                                .fullname(request.getToAddress())
                                                .build())
                                        .contact(YandexClientDto.PointContact.builder()
                                                .name(request.getClientName())
                                                .phone(request.getClientPhone())
                                                .build())
                                        .skip_confirmation(true)
                                        .build()
                        ))
                        .emergency_contact(YandexClientDto.EmergencyContact.builder()
                                .name(request.getRestaurantName())
                                .phone(request.getRestaurantPhone())
                                .build())
                        .client_requirements(YandexClientDto.ClientRequirements.builder()
                                .taxi_class("express")
                                .build())
                        .comment("Заказ #" + request.getOrderId())
                        .build();

        // Создаём и подтверждаем заявку
        YandexClientDto.ClaimResponse claim = yandexClient.createClaim(claimRequest);
        YandexClientDto.ClaimResponse confirmed = waitAndAccept(claim.getId());

        // Извлекаем цену
        BigDecimal deliveryFee = BigDecimal.valueOf(100);
        if (confirmed.getPricing() != null
                && confirmed.getPricing().getOffer() != null
                && confirmed.getPricing().getOffer().getPrice() != null) {
            deliveryFee = new BigDecimal(confirmed.getPricing().getOffer().getPrice());
        }

        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .restaurantId(request.getRestaurantId())
                .clientId(request.getClientId())
                .status(mapYandexStatus(confirmed.getStatus()))
                .fromAddress(request.getFromAddress())
                .fromLat(request.getFromLat())
                .fromLng(request.getFromLng())
                .toAddress(request.getToAddress())
                .toLat(request.getToLat())
                .toLng(request.getToLng())
                .yandexDeliveryId(confirmed.getId())
                .yandexTrackingUrl("https://go.yandex/tracking/" + confirmed.getId())
                .deliveryFee(deliveryFee)
                .estimatedMinutes(confirmed.getEta() != null ? confirmed.getEta() : 30)
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        Map<String, String> event = new HashMap<>();
        event.put("orderId", saved.getOrderId().toString());
        event.put("clientId", saved.getClientId().toString());
        rabbitTemplate.convertAndSend("delivery.created", event);

        return toResponse(saved);
    }

    private YandexClientDto.ClaimResponse waitAndAccept(String claimId) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(2000);
                YandexClientDto.ClaimResponse info = yandexClient.getClaimInfo(claimId);
                log.info("Статус заявки {}: {}", claimId, info.getStatus());

                if ("ready_for_approval".equals(info.getStatus())) {
                    return yandexClient.acceptClaim(claimId, info.getVersion());
                }
                if ("failed".equals(info.getStatus())
                        || "estimating_failed".equals(info.getStatus())) {
                    throw new RuntimeException("Яндекс отклонил заявку: " + claimId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прерывание при ожидании подтверждения");
            }
        }
        throw new RuntimeException("Таймаут ожидания ready_for_approval: " + claimId);
    }

    public DeliveryResponse getDeliveryStatus(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));
        return toResponse(delivery);
    }

    public void handleYandexWebhook(String yandexDeliveryId, String status) {
        //TODO заменить заглушки
        log.info("Webhook от Яндекса: {} - {}", yandexDeliveryId, status);

        Delivery delivery = deliveryRepository.findByYandexDeliveryId(yandexDeliveryId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));

        DeliveryStatus newStatus = mapYandexStatus(status);
        delivery.setStatus(newStatus);
        deliveryRepository.save(delivery);

        Map<String, String> deliveryStatusUpdatedEvent = new HashMap<>();
        deliveryStatusUpdatedEvent.put("orderId", delivery.getOrderId().toString());
        deliveryStatusUpdatedEvent.put("newStatus", newStatus.name());

        rabbitTemplate.convertAndSend("delivery.status.updated", deliveryStatusUpdatedEvent);
    }

    public void manualUpdate(UUID orderId, ManualUpdateRequest request) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));

        delivery.setStatus(request.getStatus());
        delivery.setManualMode(true);
        if (request.getCourierName() != null) delivery.setCourierName(request.getCourierName());
        if (request.getCourierPhone() != null) delivery.setCourierPhone(request.getCourierPhone());

        deliveryRepository.save(delivery);

        Map<String, String> deliveryStatusManualUpdatedEvent = new HashMap<>();
        deliveryStatusManualUpdatedEvent.put("orderId", delivery.getOrderId().toString());
        deliveryStatusManualUpdatedEvent.put("status", request.getStatus().name());
        rabbitTemplate.convertAndSend("delivery.status.updated", deliveryStatusManualUpdatedEvent);
    }


    private DeliveryStatus mapYandexStatus(String yandexStatus) {
        return switch (yandexStatus) {
            case "new" -> DeliveryStatus.PENDING;
            case "estimating" -> DeliveryStatus.ESTIMATING;
            case "ready_for_approval" -> DeliveryStatus.READY_FOR_APPROVAL;
            case "accepted" -> DeliveryStatus.CONFIRMED;
            case "performer_lookup",
                    "performer_draft" -> DeliveryStatus.SEARCHING_COURIER;
            case "performer_found" -> DeliveryStatus.COURIER_ASSIGNED;
            case "pickup_arrived",
                    "ready_for_pickup_confirmation",
                    "pickuped" -> DeliveryStatus.COURIER_AT_RESTAURANT;
            case "delivery_arrived",
                    "ready_for_delivery_confirmation" -> DeliveryStatus.COURIER_AT_CLIENT;
            case "delivered",
                    "delivered_finish" -> DeliveryStatus.DELIVERED;
            case "returning",
                    "return_arrived",
                    "returned",
                    "returned_finish" -> DeliveryStatus.RETURNING;
            case "cancelled" -> DeliveryStatus.CANCELLED;
            case "cancelled_with_payment" -> DeliveryStatus.CANCELLED_WITH_FEE;
            case "performer_not_found",
                    "failed",
                    "estimating_failed" -> DeliveryStatus.FAILED;
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
