package com.fooddelivery.catalog_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaUploadResponse {
    private String url;
    private String type;
}
