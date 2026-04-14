package com.fooddelivery.auth_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String avatarUrl;
    private String role;
}
