package com.fooddelivery.notification_service.Service;

import com.fooddelivery.notification_service.Entity.Notification;
import com.fooddelivery.notification_service.Enum.NotificationStatus;
import com.fooddelivery.notification_service.Enum.NotificationType;
import com.fooddelivery.notification_service.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(UUID userId, String to, String subject, String message, String eventType, UUID orderId){
        Notification notification = Notification.builder()
                .userId(userId)
                .recipient(to)
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(message)
                .eventType(eventType)
                .orderId(orderId)
                .build();

        notification = notificationRepository.save(notification);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);

            notification.setStatus(NotificationStatus.SENT);
            log.info("Email отправлен: {}", to);
        }catch (Exception e){
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            log.error("Ошибка при отправке email: {}", e.getMessage());
        }

        notificationRepository.save(notification);

    }

}
