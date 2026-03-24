package com.fooddelivery.payment_service.Controller;

import com.fooddelivery.payment_service.Dto.InitiatePaymentRequest;
import com.fooddelivery.payment_service.Dto.PaymentResponse;
import com.fooddelivery.payment_service.Dto.WebhookRequest;
import com.fooddelivery.payment_service.Entity.Payment;
import com.fooddelivery.payment_service.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payments/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @PostMapping("/payments/webhook/freedompay")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody WebhookRequest request,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        paymentService.handleWebhook(request, signature);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payments/{id}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable UUID id) {
        paymentService.refundPayment(id);
        return ResponseEntity.ok("Возврат выполнен");
    }

    @GetMapping("/admin/payouts")
    public ResponseEntity<List<Payment>> getPayouts() {
        return ResponseEntity.ok(paymentService.getPendingPayouts());
    }

    @PatchMapping("/admin/payouts/{id}")
    public ResponseEntity<String> markPayoutComplete(@PathVariable UUID id) {
        paymentService.markPayoutComplete(id);
        return ResponseEntity.ok("Выплата отмечена");
    }

    @GetMapping("/cafe/payments")
    public ResponseEntity<Map<String, Object>> getCafePayments(
            @RequestHeader("X-Cafe-Id") UUID restaurantId){
        return ResponseEntity.ok(paymentService.getCafeFinancialSummary(restaurantId));
    }

}
