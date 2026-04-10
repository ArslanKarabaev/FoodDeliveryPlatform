package com.fooddelivery.delivery_service.Controller;

import com.fooddelivery.delivery_service.Dto.CreateDeliveryRequest;
import com.fooddelivery.delivery_service.Dto.DeliveryResponse;
import com.fooddelivery.delivery_service.Dto.ManualUpdateRequest;
import com.fooddelivery.delivery_service.Service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/delivery/quote")
    public ResponseEntity<DeliveryResponse> getQuote(@Valid @RequestBody CreateDeliveryRequest request) {
        return ResponseEntity.ok(deliveryService.getQuote(request));
    }

    @PostMapping("/delivery/create")
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        return ResponseEntity.ok(deliveryService.createDelivery(request));
    }

    @PostMapping("/delivery/webhook/yandex")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, String> payload) {
        String yandexDeliveryId = payload.get("id");
        String status = payload.get("status");
        deliveryService.handleYandexWebhook(yandexDeliveryId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delivery/{orderId}/status")
    public ResponseEntity<DeliveryResponse> getStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryStatus(orderId));
    }

    @PostMapping("/cafe/delivery/manual-update")
    public ResponseEntity<String> manualUpdate(
            @RequestParam UUID orderId,
            @Valid @RequestBody ManualUpdateRequest request) {
        deliveryService.manualUpdate(orderId, request);
        return ResponseEntity.ok("Статус обновлён");
    }
}
