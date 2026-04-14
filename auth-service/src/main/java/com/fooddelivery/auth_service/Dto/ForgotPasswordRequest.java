package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email(message = "Неверный формат Email")
    private String email;

    @Pattern(regexp = "^(996|0)\\d{9}$", message = "Неверный формат телефона")
    private String phone;
}
