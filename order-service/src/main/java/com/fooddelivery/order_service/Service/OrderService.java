package com.fooddelivery.order_service.Service;

import com.fooddelivery.order_service.Dto.CreateOrderRequest;
import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import com.fooddelivery.order_service.Mapper.OrderMapper;
import com.fooddelivery.order_service.Repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    public OrderResponse createOrder(UUID clientId, CreateOrderRequest request) {
        BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal commission = subtotal.multiply(BigDecimal.valueOf(0.12));

        BigDecimal deliveryFee = BigDecimal.valueOf(100);

        BigDecimal total = subtotal.add(deliveryFee);

        List<Map<String, Object>> items = request.getItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("itemId", item.getItemId());
                    map.put("name", item.getName());
                    map.put("price", item.getPrice());
                    map.put("quantity", item.getQuantity());
                    return map;
                }).toList();

        Map<String, Object> address = new HashMap<>();
        address.put("street", request.getDeliveryAddress().getStreet());
        address.put("city", request.getDeliveryAddress().getCity());
        address.put("apartment", request.getDeliveryAddress().getApartment());
        address.put("lat", request.getDeliveryAddress().getLat());
        address.put("lng", request.getDeliveryAddress().getLng());
        address.put("comment", request.getDeliveryAddress().getComment());

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .clientId(clientId)
                .restaurantId(request.getRestaurantId())
                .status(OrderStatus.PENDING)
                .items(items)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .platformCommission(commission)
                .totalAmount(total)
                .deliveryAddress(address)
                .build();

        Order saved = orderRepository.save(order);

        rabbitTemplate.convertAndSend("order.created",
                saved.getId().toString() + ":" + saved.getClientId().toString());

        return orderMapper.toResponse(saved);
    }

    public OrderResponse getOrder(UUID orderId) {
        return orderMapper.toResponse(
                orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Заказ не найден"))
        );
    }

    public List<OrderResponse> getMyOrders(UUID clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void cancelOrder(UUID orderId, UUID clientId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getClientId().equals(clientId)) {
            throw new RuntimeException("Нет доступа");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Можно отменить только в статусе PENDING");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledReason("Отменён клиентом");
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        long count = orderRepository.count() + 1;
        return String.format("#BK-2026-%05d", count);
    }

    public UUID getClientIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractUserId(token);
    }

}
