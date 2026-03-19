package com.fooddelivery.catalog_service.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CertificateUploadRequest {
    @NotBlank
    private String name;
    private String expireDate;
}
