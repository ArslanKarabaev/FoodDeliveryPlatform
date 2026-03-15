package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.*;
import com.fooddelivery.catalog_service.Service.CafeAdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cafe")
@RequiredArgsConstructor
public class CafeAdminController {
    private final CafeAdminService cafeAdminService;

    @PutMapping("/profile")
    public ResponseEntity<RestaurantResponse> updateProfile(
            @Valid @RequestBody UpdateRestaurantRequest request,
            HttpServletRequest httpRequest){
            UUID cafeId = cafeAdminService.getCafeIdFromToken(httpRequest);
        return ResponseEntity.ok(cafeAdminService.updateProfile(cafeId, request));
    }

    @PostMapping("/menu/categories")
    public ResponseEntity<MenuCategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            HttpServletRequest httpRequest) {
        UUID cafeId = cafeAdminService.getCafeIdFromToken(httpRequest);
        return ResponseEntity.ok(cafeAdminService.createCategory(cafeId, request));
    }

    @PutMapping("/menu/categories/{id}")
    public ResponseEntity<MenuCategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(cafeAdminService.updateCategory(id, request));
    }

    @DeleteMapping("/menu/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {
        cafeAdminService.deleteCategory(id);
        return ResponseEntity.ok("Категория удалена");
    }

    @PostMapping("/menu/items")
    public ResponseEntity<MenuItemResponse> createItem(
            @Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.ok(cafeAdminService.createItem(request));
    }

    @PutMapping("/menu/items/{id}")
    public ResponseEntity<MenuItemResponse> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.ok(cafeAdminService.updateItem(id, request));
    }

    @PatchMapping("/menu/items/{id}/availability")
    public ResponseEntity<String> updateAvailability(
            @PathVariable UUID id,
            @RequestParam boolean available) {
        cafeAdminService.updateAvailability(id, available);
        return ResponseEntity.ok("Статус обновлён");
    }

}

