package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @NotBlank(message = "Пароль обязателен")
    private String password;

}
