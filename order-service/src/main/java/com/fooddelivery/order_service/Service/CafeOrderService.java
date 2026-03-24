package com.fooddelivery.order_service.Service;

import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import com.fooddelivery.order_service.Mapper.OrderMapper;
import com.fooddelivery.order_service.Repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CafeOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    public List<OrderResponse> getOrders(UUID restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Можно подтвердить только оплаченный заказ");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setCafeConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);

        Map<String, String> event = new HashMap<>();
        event.put("orderId", order.getId().toString());
        event.put("clientId", order.getClientId().toString());
        rabbitTemplate.convertAndSend("order.confirmed", event);
    }

    public void markReady(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.CONFIRMED &&
                order.getStatus() != OrderStatus.COOKING) {
            throw new RuntimeException("Неверный статус заказа");
        }

        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);

        Map<String, String> readyEvent = new HashMap<>();
        readyEvent.put("orderId", order.getId().toString());
        readyEvent.put("clientId", order.getClientId().toString());
        rabbitTemplate.convertAndSend("order.ready", readyEvent);
    }

    public void cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.DELIVERING ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя отменить заказ в статусе " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledReason(reason);
        orderRepository.save(order);

        Map<String, String> cancelEvent = new HashMap<>();
        cancelEvent.put("orderId", order.getId().toString());
        cancelEvent.put("clientId", order.getClientId().toString());
        cancelEvent.put("reason", reason);
        rabbitTemplate.convertAndSend("order.cancelled", cancelEvent);
    }

    public UUID getRestaurantIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractCafeId(token);
    }

}