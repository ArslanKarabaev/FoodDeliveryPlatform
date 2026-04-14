package com.fooddelivery.notification_service.Service;

import com.fooddelivery.notification_service.Repository.TelegramBindingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpNotificationListener {

    private final OtpTelegramBot telegramBot;
    private final TelegramBindingRepository bindingRepository;
    @RabbitListener(queues = "otp.send")
    public void handleOtpSend(Map<String, String> event) {
        String phone = event.get("phone");
        String code = event.get("code");

        bindingRepository.findByPhone(phone).ifPresentOrElse(
                binding -> {
                    telegramBot.sendOtp(binding.getChatId(), code);
                    log.info("OTP отправлен в Telegram, номер {}", phone);
                },
                () -> log.warn("Telegram не привязан для номера {}. Код: {}", phone, code)
        );
    }
}