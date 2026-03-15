package com.fooddelivery.order_service.Controller;

import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Service.CafeOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cafe/orders")
@RequiredArgsConstructor
public class CafeOrderController {

    private final CafeOrderService cafeOrderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            HttpServletRequest httpRequest) {
    UUID restaurantId = cafeOrderService.getRestaurantIdFromToken(httpRequest);
        return ResponseEntity.ok(cafeOrderService.getOrders(restaurantId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable UUID id) {
        cafeOrderService.confirmOrder(id);
        return ResponseEntity.ok("Заказ подтверждён");
    }

    @PatchMapping("/{id}/ready")
    public ResponseEntity<String> markReady(@PathVariable UUID id) {
        cafeOrderService.markReady(id);
        return ResponseEntity.ok("Заказ готов");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable UUID id,
            @RequestParam String reason) {
        cafeOrderService.cancelOrder(id, reason);
        return ResponseEntity.ok("Заказ отклонён");
    }
}