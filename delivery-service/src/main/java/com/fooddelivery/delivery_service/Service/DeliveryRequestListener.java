package com.fooddelivery.delivery_service.Service;

import com.fooddelivery.delivery_service.Dto.CreateDeliveryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryRequestListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "delivery.create.request")
    public void handleDeliveryRequest(Map<String, Object> event) {
        log.info("Получен запрос на создание доставки для заказа: {}",
                event.get("orderId"));

        try {
            CreateDeliveryRequest request = CreateDeliveryRequest.builder()
                    .orderId(UUID.fromString((String) event.get("orderId")))
                    .restaurantId(UUID.fromString((String) event.get("restaurantId")))
                    .clientId(UUID.fromString((String) event.get("clientId")))
                    .fromAddress((String) event.get("fromAddress"))
                    .fromLat(toDouble(event.get("fromLat")))
                    .fromLng(toDouble(event.get("fromLng")))
                    .restaurantName((String) event.get("restaurantName"))
                    .restaurantPhone((String) event.get("restaurantPhone"))
                    .toAddress((String) event.get("toAddress"))
                    .toLat(toDouble(event.get("toLat")))
                    .toLng(toDouble(event.get("toLng")))
                    .clientName((String) event.get("clientName"))
                    .clientPhone((String) event.get("clientPhone"))
                    .orderAmount((String) event.get("orderAmount"))
                    .orderItemsCount(event.get("orderItemsCount") != null
                            ? Integer.valueOf(event.get("orderItemsCount").toString()) : 1)
                    .orderDescription((String) event.get("orderDescription"))
                    .build();

            deliveryService.createDelivery(request);

        } catch (Exception e) {
            log.error("Ошибка создания доставки для заказа {}: {}",
                    event.get("orderId"), e.getMessage());
        }
    }

    private Double toDouble(Object value) {
        if (value == null) return 0.0;
        return Double.valueOf(value.toString());
    }
}
