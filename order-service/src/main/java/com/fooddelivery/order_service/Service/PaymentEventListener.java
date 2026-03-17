package com.fooddelivery.order_service.Service;

import com.fooddelivery.order_service.Entity.Order;
import com.fooddelivery.order_service.Enum.OrderStatus;
import com.fooddelivery.order_service.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = "payment.completed")
    public void handlePaymentCompleted(String orderIdStr){
        log.info("Получено событие payment.completed для заказа: {}", orderIdStr);

        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Заказ не найден"));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Статус заказа {} изменен на PAID", orderId);
    }

    @RabbitListener(queues = "payment.failed")
    public void handlePaymentFailed(String orderIdStr){
        log.info("Получено событие payment.failed  для заказа {}", orderIdStr);

        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Заказ не найден"));

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledReason("Оплата не прошла");
        orderRepository.save(order);

        log.info("Статус заказа {} изменен на CANCELED", orderId);
    }

    @RabbitListener(queues = "payment.refunded")
    public void handlePaymentRefunded(String orderIdStr) {
        log.info("Получено событие payment.refunded для заказа: {}", orderIdStr);

        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);

        log.info("Заказ {} переведён в статус REFUNDED", orderId);
    }

}
