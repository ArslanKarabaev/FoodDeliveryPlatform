package com.fooddelivery.notification_service.Repository;

import com.fooddelivery.notification_service.Entity.TelegramBinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TelegramBindingRepository extends JpaRepository<TelegramBinding, UUID> {
    Optional<TelegramBinding> findByPhone(String phone);
}
