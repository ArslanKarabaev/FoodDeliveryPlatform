package com.fooddelivery.payment_service.Repository;

import com.fooddelivery.payment_service.Entity.Payment;
import com.fooddelivery.payment_service.Enum.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);

    boolean existsByProviderPaymentId(String providerPaymentId);

    List<Payment> findByPayoutStatus(PayoutStatus payoutStatus);

}
