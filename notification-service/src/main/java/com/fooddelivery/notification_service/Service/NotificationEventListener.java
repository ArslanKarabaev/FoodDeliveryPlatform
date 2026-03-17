package com.fooddelivery.notification_service.Service;

import com.fooddelivery.notification_service.Client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final EmailService emailService;
    private final AuthServiceClient authServiceClient;

    private String[] parseMessage(String message){
        return message.split(":");
    }

    @RabbitListener(queues = "order.created")
    public void handleOrderCreated(String message){
        log.info("Получено событие order.created: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Заказ создан",
                    "Ваш заказ создан и ожидает оплаты.",
                    "order.created",
                    orderId
            );
        } catch (Exception e){
            log.error("Ошибка отправки email: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "order.confirmed")
    public void handleOrderConfirmed(String message){
        log.info("Получено событие order.confirmed: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Заказ подтвержден",
                    "Ресторан подтвердил ваш заказ и начал готовить",
                    "order.confirmed",
                    orderId
            );
        } catch (Exception e){
            log.error("Ошибка отправки email: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "order.cancelled")
    public void handleOrderCancelled(String message){
        log.info("Получено событие order.cancelled: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Заказ отменен",
                    "Ваз заказ был отменен",
                    "order.cancelled",
                    orderId
            );
        } catch (Exception e){
            log.error("Ошибка отправки email: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.completed")
    public void handlePaymentCompleted(String message) {
        log.info("Получено событие payment.completed: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Оплата прошла успешно",
                    "Ваш заказ оплачен! Ресторан уже готовит его.",
                    "payment.completed",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.failed")
    public void handlePaymentFailed(String message) {
        log.info("Получено событие payment.failed: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Оплата не прошла",
                    "К сожалению оплата вашего заказа не прошла. Попробуйте снова.",
                    "payment.failed",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "delivery.created")
    public void handleDeliveryCreated(String message) {
        log.info("Получено событие delivery.created: {}", message);
        String[] parts = parseMessage(message);
        UUID orderId = UUID.fromString(parts[0]);
        UUID clientId = UUID.fromString(parts[1]);

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Курьер назначен",
                    "Курьер назначен и скоро заберёт ваш заказ.",
                    "delivery.created",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "delivery.status.updated")
    public void handleDeliveryStatusUpdated(String message) {
        log.info("Получено событие delivery.status.updated: {}", message);

        String[] parts = message.split(":");
        UUID orderId = UUID.fromString(parts[0]);
        String status = parts[1];

        log.info("Статус доставки для заказа {} изменён на {}", orderId, status);
        // Здесь можно добавить уведомление о статусе доставки
    }


}
