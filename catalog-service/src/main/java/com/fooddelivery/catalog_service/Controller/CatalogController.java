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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(catalogService.getRestaurants(city, cuisineType, pageable));
    }

    @GetMapping("/restaurants/{slug}")
    public ResponseEntity<RestaurantResponse> getRestaurantBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getRestaurantBySlug(slug));
    }

    @GetMapping("/restaurants/{id}/menu")
    public ResponseEntity<List<MenuCategoryResponse>> getMenu(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.getMenu(id));
    }

    @PostMapping("/reviews")
    public ResponseEntity<String> addReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UUID clientId) {
        reviewService.addReview(request, clientId);
        return ResponseEntity.ok("Отзыв добавлен");
    }

    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(reviewService.getReviews(restaurantId));
    }

}
