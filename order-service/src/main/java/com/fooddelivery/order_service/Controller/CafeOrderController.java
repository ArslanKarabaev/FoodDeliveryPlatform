package com.fooddelivery.order_service.Controller;

import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Service.CafeOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cafe/orders")
@RequiredArgsConstructor
public class CafeOrderController {

    private final CafeOrderService cafeOrderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getCafeOrders(
            @AuthenticationPrincipal Map<String, Object> principal,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        UUID cafeId = (UUID) principal.get("cafeId");
        return ResponseEntity.ok(cafeOrderService.getOrders(cafeId,status,page,size));
    }

    @GetMapping("/cafe/orders/counts")
    public ResponseEntity<Map<String, Long>> getOrderCounts(
            @AuthenticationPrincipal Map<String, Object> principal) {
        UUID cafeId = (UUID) principal.get("cafeId");
        return ResponseEntity.ok(cafeOrderService.getOrderCounts(cafeId));
    }

    @PostMapping("/cafe/orders/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal Map<String, Object> principal) {

        UUID cafeId = (UUID) principal.get("cafeId");
        return ResponseEntity.ok(cafeOrderService.confirmOrderByCafe(id, cafeId));
    }

    @PatchMapping("/{id}/ready")
    public ResponseEntity<String> markReady(@PathVariable UUID id) {
        cafeOrderService.markReady(id);
        return ResponseEntity.ok("Заказ готов");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable UUID id,
            @RequestParam String reason,
            @AuthenticationPrincipal Map<String, Object> principal) {
        UUID cafeId = (UUID) principal.get("cafeId");
        cafeOrderService.cancelOrder(id,cafeId, reason);
        return ResponseEntity.ok("Заказ отклонён");
    }
}