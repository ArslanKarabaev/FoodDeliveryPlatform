package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^(996|0)\\d{9}$", message = "Неверный формат телефона")
    private String phone;

    @NotBlank(message = "Код обязателен")
    @Size(min = 4, max = 4, message = "Код должен быть 4 символа")
    private String code;
}
