package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.CreateRestaurantRequest;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Service.AdminCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCatalogController {
    private final AdminCatalogService adminCatalogService;

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {
        System.out.println("CREATE RESTAURANT REQUEST: " + request.getName());
        return ResponseEntity.ok(adminCatalogService.createRestaurant(request));
    }

    @PatchMapping("/restaurants/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        adminCatalogService.updateRestaurantStatus(id, active);
        return ResponseEntity.ok("Статус обновлён");
    }
}
