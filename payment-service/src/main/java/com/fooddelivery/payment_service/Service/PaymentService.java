package com.fooddelivery.payment_service.Service;

import com.fooddelivery.payment_service.Dto.InitiatePaymentRequest;
import com.fooddelivery.payment_service.Dto.PaymentResponse;
import com.fooddelivery.payment_service.Dto.WebhookRequest;
import com.fooddelivery.payment_service.Entity.Payment;
import com.fooddelivery.payment_service.Enum.PaymentStatus;
import com.fooddelivery.payment_service.Enum.PayoutStatus;
import com.fooddelivery.payment_service.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${freedompay.secret.key}")
    private String secretKey;

    @Value("${freedompay.api.url}")
    private String apiUrl;

    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Платёж для этого заказа уже существует");
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .restaurantId(request.getRestaurantId())
                .clientId(request.getClientId())
                .amount(request.getAmount())
                .platformFee(request.getPlatformFee())
                .restaurantPayout(request.getRestaurantPayout())
                .status(PaymentStatus.CREATED)
                .payoutStatus(PayoutStatus.PENDING)
                .build();

        //TODO подключить реальную ссылку на фридом
        String paymentUrl = apiUrl + "/pay?orderId=" + request.getOrderId() + "&amount=" + request.getAmount();
        payment.setPaymentUrl(paymentUrl);

        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .id(saved.getId())
                .orderId(saved.getOrderId())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .paymentUrl(saved.getPaymentUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public void handleWebhook(WebhookRequest request, String rawSignature) {
        if (paymentRepository.existsByProviderPaymentId(request.getProviderPaymentId())) {
            log.warn("Webhook уже обработан: {}", request.getProviderPaymentId());
            return;
        }

        //TODO вернуть при деплое в прод
//        if (!verifySignature(request, rawSignature)) {
//            throw new RuntimeException("Неверная подпись webhook");
//        }

        Payment payment = paymentRepository.findByOrderId(
                        UUID.fromString(request.getData().get("orderId").toString()))
                .orElseThrow(() -> new RuntimeException("Платёж не найден"));

        payment.setProviderPaymentId(request.getProviderPaymentId());
        payment.setProviderResponse(request.getData());

        if ("SUCCESS".equals(request.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            Map<String, String> paymentCompletedEvent = new HashMap<>();
            paymentCompletedEvent.put("orderId", payment.getOrderId().toString());
            paymentCompletedEvent.put("clientId", payment.getClientId().toString());

            rabbitTemplate.convertAndSend("payment.completed", paymentCompletedEvent);
            rabbitTemplate.convertAndSend("payment.completed.not", paymentCompletedEvent);
            log.info("Платеж завершен: {}", payment.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            Map<String, String> paymentFailedEvent = new HashMap<>();
            paymentFailedEvent.put("orderId", payment.getOrderId().toString());
            paymentFailedEvent.put("clientId", payment.getClientId().toString());
            rabbitTemplate.convertAndSend("payment.failed", paymentFailedEvent);
            rabbitTemplate.convertAndSend("payment.failed.not", paymentFailedEvent);
            log.info("Платеж не прошел: {}", payment.getOrderId());
        }
    }

    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Платеж не найден"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Можно вернуть только завершенный платеж");
        }

        //TODO реализоать реальный запрос к фридомпэй
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Map<String, String> paymentRefundedEvent = new HashMap<>();
        paymentRefundedEvent.put("orderId", payment.getOrderId().toString());
        paymentRefundedEvent.put("clientId", payment.getClientId().toString());

        rabbitTemplate.convertAndSend("payment.refunded", paymentRefundedEvent);
        rabbitTemplate.convertAndSend("payment.refunded.not", paymentRefundedEvent);
    }

    public void markPayoutComplete(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Платеж не найден"));

        payment.setPayoutStatus(PayoutStatus.PAID_OUT);
        payment.setPayoutAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    public List<Payment> getPendingPayouts() {
        return paymentRepository.findByPayoutStatus(PayoutStatus.PENDING);
    }

    public Map<String, Object> getCafeFinancialSummary(UUID restaurantId){
        List<Payment> payments = paymentRepository.findByRestaurantId(restaurantId);

        BigDecimal totalRevenue = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getRestaurantPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingPayout = payments.stream()
                .filter(p-> p.getStatus() == PaymentStatus.COMPLETED
                && p.getPayoutStatus() == PayoutStatus.PENDING)
                .map(Payment::getRestaurantPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidOut = payments.stream()
                .filter(p -> p.getPayoutStatus() == PayoutStatus.PAID_OUT)
                .map(Payment::getRestaurantPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long completedCount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .count();

        long refundedCount = payments.stream()
                .filter(p->p.getStatus() == PaymentStatus.REFUNDED)
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("restaurantId", restaurantId);
        summary.put("totalRevenue", totalRevenue);
        summary.put("pendingPayout", pendingPayout);
        summary.put("paidOut", paidOut);
        summary.put("completedPayments", completedCount);
        summary.put("refundedPayments", refundedCount);
        summary.put("recentPayments", payments.stream()
                .sorted((a,b)-> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .toList());

        return summary;
    }

    private boolean verifySignature(WebhookRequest request, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            String data = request.getProviderPaymentId() + request.getStatus();
            byte[] hash = mac.doFinal(data.getBytes());
            String expected = Base64.getEncoder().encodeToString(hash);
            return expected.equals(signature);
        } catch (Exception e) {
            log.error("Ошибка верификации подписи", e);
            return false;
        }
    }


}
