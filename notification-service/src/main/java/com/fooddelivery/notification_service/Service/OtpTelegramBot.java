package com.fooddelivery.notification_service.Service;

import com.fooddelivery.notification_service.Entity.TelegramBinding;
import com.fooddelivery.notification_service.Repository.TelegramBindingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class OtpTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final TelegramBindingRepository bindingRepository;

    public OtpTelegramBot(TelegramBindingRepository bindingRepository,
                          @Value("${telegram.bot.token}") String botToken,
                          @Value("${telegram.bot.username}") String botUsername) {
        this.bindingRepository = bindingRepository;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();

        if ("/start".equals(text)) {
            sendMessage(chatId, "Привет! Отправь свой номер: +996XXXXXXXXX");
            return;
        }

        if (text != null && text.matches("^\\+?996\\d{9}$|^0\\d{9}$")) {
            String normalizedPhone = normalizePhone(text);

            // Ищем существующий binding или создаём новый
            TelegramBinding binding = bindingRepository.findByPhone(normalizedPhone)
                    .orElse(TelegramBinding.builder().phone(normalizedPhone).build());

            binding.setChatId(chatId);
            bindingRepository.save(binding);

            sendMessage(chatId, "✅ Номер привязан! Коды будут приходить сюда.");
            return;
        }

        sendMessage(chatId, "Отправь номер телефона для привязки.");
    }

    public void sendOtp(Long chatId, String code) {
        sendMessage(chatId, "🔐 Ваш код: *" + code + "*\n\nДействителен 5 минут.");
    }

    private void sendMessage(Long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки в Telegram: {}", e.getMessage());
        }
    }

    private String normalizePhone(String phone) {
        return phone.startsWith("+") ? phone.substring(1) : phone;
    }
}