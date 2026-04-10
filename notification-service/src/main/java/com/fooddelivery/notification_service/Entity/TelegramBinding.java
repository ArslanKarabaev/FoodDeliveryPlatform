package com.fooddelivery.notification_service.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "telegram_bindings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramBinding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String phone;

    private Long chatId;

}
