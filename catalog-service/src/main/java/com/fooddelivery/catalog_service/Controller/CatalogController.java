package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.MenuCategoryResponse;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import com.fooddelivery.catalog_service.Service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

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
    public ResponseEntity<RestaurantResponse> getRestaurantBySlug(@PathVariable String slug){
        return ResponseEntity.ok(catalogService.getRestaurantBySlug(slug));
    }

    @GetMapping("/restaurants/{id}/menu")
    public ResponseEntity<List<MenuCategoryResponse>> getMenu(@PathVariable UUID id){
        return ResponseEntity.ok(catalogService.getMenu(id));
    }

}
