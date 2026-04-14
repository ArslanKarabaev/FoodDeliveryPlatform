package com.fooddelivery.catalog_service.Controller;

import com.fooddelivery.catalog_service.Dto.*;
import com.fooddelivery.catalog_service.Service.CafeAdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @Validated(CreateMenuItemRequest.OnUpdate.class)
            @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.ok(cafeAdminService.updateItem(id, request));
    }

    @PatchMapping("/menu/items/{id}/availability")
    public ResponseEntity<String> updateAvailability(
            @PathVariable UUID id,
            @RequestParam boolean available) {
        cafeAdminService.updateAvailability(id, available);
        return ResponseEntity.ok("Статус обновлён");
    }

    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadMedia(
            @RequestParam MultipartFile file,
            @RequestParam String type){
        MediaUploadResponse response = cafeAdminService.uploadMedia(file, type);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadCertificate(
            @RequestParam MultipartFile file,
            @RequestParam String name,
            @RequestParam(required = false) String expiryDate,
            HttpServletRequest httpRequest) {

        UUID cafeId = cafeAdminService.getCafeIdFromToken(httpRequest);
        MediaUploadResponse response = cafeAdminService.uploadCertificate(file, name, expiryDate, cafeId);
        return ResponseEntity.ok(response);
    }
}

