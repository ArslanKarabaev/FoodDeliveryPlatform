package com.fooddelivery.order_service.Controller;

import com.fooddelivery.order_service.Dto.CreateOrderRequest;
import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        UUID clientId = orderService.getClientIdFromToken(httpRequest);
        return ResponseEntity.ok(orderService.createOrder(clientId, request));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            HttpServletRequest httpRequest) {
        UUID clientId = orderService.getClientIdFromToken(httpRequest);
        return ResponseEntity.ok(orderService.getMyOrders(clientId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        UUID clientId = orderService.getClientIdFromToken(httpRequest);
        orderService.cancelOrder(id, clientId);
        return ResponseEntity.ok("Заказ отменён");
    }

}
