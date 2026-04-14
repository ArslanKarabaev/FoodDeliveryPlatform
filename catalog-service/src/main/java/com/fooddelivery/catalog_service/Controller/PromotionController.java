package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.CreatePromotionRequest;
import com.fooddelivery.catalog_service.Entity.Promotion;
import com.fooddelivery.catalog_service.Service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/cafe/promotions")
    public ResponseEntity<Promotion> createPromotion(
            @Valid @RequestBody CreatePromotionRequest request,
            @AuthenticationPrincipal Map<String, Object> principal) {

        UUID restaurantId = (UUID) principal.get("cafeId");
        return ResponseEntity.ok(promotionService.createPromotion(request, restaurantId));
    }

    @DeleteMapping("/cafe/promotions/{id}")
    public ResponseEntity<String> deactivatePromotion(
            @PathVariable UUID id,
            @AuthenticationPrincipal Map<String, Object> principal) {

        UUID restaurantId = (UUID) principal.get("cafeId");
        promotionService.deactivatePromotion(id, restaurantId);
        return ResponseEntity.ok("Акция деактивирована");
    }

    @GetMapping("/promotions")
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllActivePromotions());
    }

    @GetMapping("/restaurants/{restaurantId}/promotions")
    public ResponseEntity<List<Promotion>> getRestaurantPromotions(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(promotionService.getRestaurantPromotions(restaurantId));
    }

    @GetMapping("/restaurants/with-promotions")
    public ResponseEntity<List<UUID>> getRestaurantsWithPromotions() {
        return ResponseEntity.ok(promotionService.getRestaurantIdsWithPromotions());
    }
}