package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.CreateReviewRequest;
import com.fooddelivery.catalog_service.Dto.MenuCategoryResponse;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.Review;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import com.fooddelivery.catalog_service.Service.CatalogService;
import com.fooddelivery.catalog_service.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;
    private final ReviewService reviewService;

    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) CuisineType cuisineType,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer maxDeliveryTime,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Sort sort = switch (sortBy != null ? sortBy : "") {
            case "rating" -> Sort.by(Sort.Direction.DESC, "averageRating");
            case "deliveryTime" -> Sort.by(Sort.Direction.ASC, "estimatedDeliveryMinutes");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(catalogService.getRestaurants(
                city, cuisineType, minRating, maxDeliveryTime, pageable));
    }

    @GetMapping("/restaurants/{slug}")
    public ResponseEntity<RestaurantResponse> getRestaurantBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getRestaurantBySlug(slug));
    }

    @GetMapping("/restaurants/{id}/menu")
    public ResponseEntity<List<MenuCategoryResponse>> getMenu(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.getMenu(id));
    }

    @GetMapping("/restaurants/{id}/menu/tag")
    public ResponseEntity<List<MenuCategoryResponse>> getMenuByTag(
            @PathVariable UUID id,
            @RequestParam(required = false) String tag) {
        return ResponseEntity.ok(catalogService.getMenuByTag(id, tag));
    }

    @PostMapping("/reviews")
    public ResponseEntity<String> addReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Map<String, Object> principal) {
        UUID clientId = (UUID) principal.get("userId");
        reviewService.addReview(request, clientId);
        return ResponseEntity.ok("Отзыв добавлен");
    }

    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<List<Review>> getRestaurantReviews(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(reviewService.getRestaurantReviews(restaurantId));
    }

    @GetMapping("/menu-items/{menuItemId}/reviews")
    public ResponseEntity<List<Review>> getMenuItemReviews(@PathVariable UUID menuItemId) {
        return ResponseEntity.ok(reviewService.getMenuItemReviews(menuItemId));
    }

    @GetMapping("/restaurants/recommended")
    public ResponseEntity<List<RestaurantResponse>> getRecommendations(
            @AuthenticationPrincipal Map<String, Object> principal) {

        UUID clientId = (UUID) principal.get("userId");
        return ResponseEntity.ok(catalogService.getRecommendations(clientId));
    }

}
