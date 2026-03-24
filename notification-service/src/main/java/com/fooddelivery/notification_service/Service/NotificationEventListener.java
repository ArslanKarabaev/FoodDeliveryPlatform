package com.fooddelivery.notification_service.Service;

import com.fooddelivery.notification_service.Client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final EmailService emailService;
    private final AuthServiceClient authServiceClient;

    @RabbitListener(queues = "order.created")
    public void handleOrderCreated(Map<String, String> event){
        log.info("Получено событие order.created: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

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
    public void handleOrderConfirmed(Map<String, String> event){
        log.info("Получено событие order.confirmed: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString("clientId");

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

    @RabbitListener(queues = "order.ready")
    public void handleOrderReady(Map<String, String> event){
        log.info("Получено событие order.ready: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Заказ готов",
                    "Ваш заказ готов и ожидает курьера.",
                    "order.ready",
                    orderId
            );
        } catch (Exception e){
            log.error("Ошибка отправки email: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "order.cancelled")
    public void handleOrderCancelled(Map<String, String> event){
        log.info("Получено событие order.cancelled: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));
        String reason = event.get("reason");

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Заказ отменен",
                    reason,
                    "order.cancelled",
                    orderId
            );
        } catch (Exception e){
            log.error("Ошибка отправки email: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.completed.not")
    public void handlePaymentCompletedNot(Map<String, String> event) {
        log.info("Получено событие payment.completed.not: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Оплата прошла успешно",
                    "Ваш заказ оплачен! Ресторан уже готовит его.",
                    "payment.completed.not",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.failed.not")
    public void handlePaymentFailed(Map<String, String> event) {
        log.info("Получено событие payment.failed.not: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Оплата не прошла",
                    "К сожалению оплата вашего заказа не прошла. Попробуйте снова.",
                    "payment.failed.not",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.refunded.not")
    public void handlePaymentRefunded(Map<String, String> event) {
        log.info("Получено событие payment.refunded.not: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

        try {
            String email = authServiceClient.getUserEmail(clientId);
            emailService.sendEmail(
                    clientId,
                    email,
                    "Оплата возвращена",
                    "Ваша оплата успешно возвращена",
                    "payment.refunded.not",
                    orderId
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "delivery.created")
    public void handleDeliveryCreated(Map<String, String> event) {
        log.info("Получено событие delivery.created: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        UUID clientId = UUID.fromString(event.get("clientId"));

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
    public void handleDeliveryStatusUpdated(Map<String, String> event) {
        log.info("Получено событие delivery.status.updated: {}", event);
        UUID orderId = UUID.fromString(event.get("orderId"));
        String status = event.get("status");

        log.info("Статус доставки для заказа {} изменён на {}", orderId, status);
    }

    @RabbitListener(queues = "password.reset")
    public void handlePasswordReset(Map<String, String> event) {
        String email = event.get("email");
        UUID clientId = UUID.fromString(event.get("clientId"));
        String resetLink = event.get("resetLink");
        try {
            emailService.sendEmail(
                    clientId,
                    email,
                    "Сброс пароля",
                    "Для сброса пароля перейдите по ссылке:\n" + resetLink + "\n\nСсылка действительна 1 час.",
                    "password.reset",
                    null
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления: {}", e.getMessage());
        }
    }


}
