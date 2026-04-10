package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^(996|0)\\d{9}$", message = "Неверный формат телефона")
    private String phone;
}
