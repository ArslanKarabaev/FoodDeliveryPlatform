package com.fooddelivery.order_service.Service;

import com.fooddelivery.order_service.Client.CatalogServiceClient;
import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import com.fooddelivery.order_service.Mapper.OrderMapper;
import com.fooddelivery.order_service.Repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private static final Logger log = LoggerFactory.getLogger(CafeOrderService.class);
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;
    private final CatalogServiceClient catalogServiceClient;

    public Page<OrderResponse> getOrders(UUID cafeId, String status, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if(status != null && !status.isBlank()){
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtDesc(cafeId, orderStatus, pageable);
        }else {
            orders = orderRepository.findByRestaurantIdOrderByCreatedAtDesc(cafeId, pageable);
        }

        return orders.map(orderMapper::toResponse);
    }

    public Map<String, Long> getOrderCounts(UUID cafeId) {
        List<Object[]> raw = orderRepository.countByRestaurantIdGroupByStatus(cafeId);
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : raw) {
            counts.put(((OrderStatus) row[0]).name(), (Long) row[1]);
        }
        return counts;
    }


    public OrderResponse confirmOrderByCafe(UUID orderId, UUID cafeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getRestaurantId().equals(cafeId)) {
            throw new RuntimeException("Нет доступа к этому заказу");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Можно подтвердить только заказ в статусе PAID");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setCafeConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);

        publishDeliveryRequest(order);

        return orderMapper.toResponse(order);
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

    public void cancelOrder(UUID orderId,UUID cafeId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getRestaurantId().equals(cafeId)) {
            throw new RuntimeException("Нет доступа к этому заказу");
        }

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

    private void publishDeliveryRequest(Order order) {
        CatalogServiceClient.RestaurantInfo restaurant =
                catalogServiceClient.getRestaurantInfo(order.getRestaurantId());

        Map<String, Object> addr = order.getDeliveryAddress();

        Map<String, Object> deliveryEvent = new HashMap<>();
        deliveryEvent.put("orderId", order.getId().toString());
        deliveryEvent.put("restaurantId", order.getRestaurantId().toString());
        deliveryEvent.put("clientId", order.getClientId().toString());

        deliveryEvent.put("fromAddress", restaurant.getAddress());
        deliveryEvent.put("fromLat", restaurant.getLatitude());
        deliveryEvent.put("fromLng", restaurant.getLongitude());
        deliveryEvent.put("restaurantName", restaurant.getName());
        deliveryEvent.put("restaurantPhone", restaurant.getPhone());

        String toAddress = addr.get("city") + ", " + addr.get("street");
        deliveryEvent.put("toAddress", toAddress);
        deliveryEvent.put("toLat", addr.get("lat"));
        deliveryEvent.put("toLng", addr.get("lng"));
        deliveryEvent.put("clientName", addr.get("clientName"));
        deliveryEvent.put("clientPhone", addr.get("clientPhone"));

        deliveryEvent.put("orderAmount", order.getTotalAmount().toString());
        deliveryEvent.put("orderItemsCount", getTotalItemsCount(order));
        deliveryEvent.put("orderDescription", buildOrderDescription(order));

        rabbitTemplate.convertAndSend("delivery.create.request", deliveryEvent);
        log.info("Опубликован запрос на доставку для заказа: {}", order.getId());
    }

    private int getTotalItemsCount(Order order) {
        if (order.getItems() == null) return 1;
        return order.getItems().stream()
                .mapToInt(item -> {
                    Object qty = item.get("quantity");
                    return qty != null ? Integer.parseInt(qty.toString()) : 1;
                })
                .sum();
    }

    // Строим описание для курьера: "Бургер x2, Картошка x1"
    private String buildOrderDescription(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return "Заказ из ресторана";
        }
        return order.getItems().stream()
                .map(item -> item.get("name") + " x" + item.get("quantity"))
                .collect(Collectors.joining(", "));
    }

}