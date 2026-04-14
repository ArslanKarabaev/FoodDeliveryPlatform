package com.fooddelivery.catalog_service.Controller;


import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/restaurants")
@RequiredArgsConstructor
public class RestaurantInternalController {

    private final RestaurantRepository restaurantRepository;

    @GetMapping("/{restaurantId}/contact")
    public ResponseEntity<Map<String, Object>> getRestaurantContact(
            @PathVariable UUID restaurantId) {

        return restaurantRepository.findById(restaurantId)
                .map(r -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("name", r.getName());
                    response.put("phone", r.getPhone() != null ? r.getPhone() : "");
                    response.put("address", r.getAddress() != null ? r.getAddress() : "");
                    response.put("latitude", r.getLatitude() != null ? r.getLatitude() : 0.0);
                    response.put("longitude", r.getLongitude() != null ? r.getLongitude() : 0.0);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}