package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    //@NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат Email")
    private String email;

    @NotBlank(message = "Password обязателен")
    @Size(min = 8, message = "Пароль минимум из 8 символов")
    private String password;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^(996|0)\\d{9}$",message = "Неверный формат телефона")
    private String phone;

    @Size(max = 50, message = "Имя максимум 50 символов")
    private String name;
}
