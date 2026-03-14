package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCafeAdminRequest {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат Email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль минимум 8 символов")
    private String temporaryPassword;

    private UUID cafeId;
}
