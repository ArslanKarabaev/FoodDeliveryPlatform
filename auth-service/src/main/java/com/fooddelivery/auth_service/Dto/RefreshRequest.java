package com.fooddelivery.auth_service.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "Refresh token обязателен")
    private String refreshToken;
}