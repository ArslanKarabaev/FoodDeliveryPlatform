package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "Имя не более 50 символов")
    private String name;

    @Email(message = "Неверный формат email")
    private String email;

    @Pattern(regexp = "^(996|0)\\d{9}$", message = "Неверный формат телефона")
    private String phone;

    private String currentPassword;

    @Size(min = 8, message = "Пароль минимум 8 символов")
    private String newPassword;

    private String confirmPassword;
}
