package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.*;
import com.fooddelivery.catalog_service.Entity.MenuCategory;
import com.fooddelivery.catalog_service.Entity.MenuItem;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Mapper.RestaurantMapper;
import com.fooddelivery.catalog_service.Repository.MenuCategoryRepository;
import com.fooddelivery.catalog_service.Repository.MenuItemRepository;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CafeAdminService {
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantMapper restaurantMapper;
    private final JwtService jwtService;
    private final StorageService storageService;

    public RestaurantResponse updateProfile(UUID cafeId, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getAddress() != null) restaurant.setAddress(request.getAddress());
        if (request.getCity() != null) restaurant.setCity(request.getCity());
        if (request.getLatitude() != null) restaurant.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) restaurant.setLongitude(request.getLongitude());
        if (request.getPhone() != null) restaurant.setPhone(request.getPhone());
        if (request.getEmail() != null) restaurant.setEmail(request.getEmail());
        if (request.getMinOrderAmount() != null) restaurant.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getDeliveryZoneRadiusKm() != null) restaurant.setDeliveryZoneRadiusKm(request.getDeliveryZoneRadiusKm());
        if (request.getWorkingHours() != null) restaurant.setWorkingHours(request.getWorkingHours());
        if (request.getEstimatedDeliveryMinutes() != null) restaurant.setEstimatedDeliveryMinutes(request.getEstimatedDeliveryMinutes());

        return restaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public MenuCategoryResponse createCategory(UUID cafeId, CreateCategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        MenuCategory category = MenuCategory.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .position(request.getPosition())
                .build();

        return restaurantMapper.toCategoryResponse(menuCategoryRepository.save(category));
    }

    public MenuCategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        category.setName(request.getName());
        if (request.getPosition() != null) category.setPosition(request.getPosition());

        return restaurantMapper.toCategoryResponse(menuCategoryRepository.save(category));
    }

    public void deleteCategory(UUID id) {
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        if (!menuItemRepository.findByCategoryId(id).isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию с позициями");
        }

        menuCategoryRepository.delete(category);
    }

    public MenuItemResponse createItem(CreateMenuItemRequest request) {
        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        MenuItem item = MenuItem.builder()
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .weightGrams(request.getWeightGrams())
                .allergens(request.getAllergens())
                .tags(request.getTags())
                .build();

        return restaurantMapper.toItemResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse updateItem(UUID id, CreateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Позиция не найдена"));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getWeightGrams() != null) item.setWeightGrams(request.getWeightGrams());
        if (request.getAllergens() != null) item.setAllergens(request.getAllergens());
        if (request.getTags() != null) item.setTags(request.getTags());

        return restaurantMapper.toItemResponse(menuItemRepository.save(item));
    }

    public void updateAvailability(UUID id, boolean available) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Позиция не найдена"));
        item.setAvailable(available);
        menuItemRepository.save(item);
    }

    public UUID getCafeIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractCafeId(token);
    }

    public MediaUploadResponse uploadMedia(MultipartFile file, String type) {
        validateImageFile(file);

        String folder = switch (type) {
            case "logo" -> "logos";
            case "cover" -> "covers";
            case "menu-item" -> "menu-items";
            case "menu-item-png" -> "menu-item-png";
            default -> throw new IllegalArgumentException(
                    "Неверный тип " + type + ". Доступно только logo, cover, menu-item, menu-item-png");
        };

        String url = storageService.uploadFile(file, folder);
        return new MediaUploadResponse(url, type);
    }

    public MediaUploadResponse uploadCertificate(MultipartFile file, String name,
                                                 String expiryDate, UUID cafeId) {
        validateFileSize(file, 10 * 1024 * 1024);
        String url = storageService.uploadFile(file, "certificates");

        Restaurant restaurant = restaurantRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        List<Map<String, String>> certificates = restaurant.getCertificates();
        if (certificates == null) certificates = new ArrayList<>();

        Map<String, String> cert = new HashMap<>();
        cert.put("name", name);
        cert.put("url", url);
        cert.put("expiryDate", expiryDate);
        cert.put("uploadedAt", LocalDateTime.now().toString());
        certificates.add(cert);

        restaurant.setCertificates(certificates);
        restaurantRepository.save(restaurant);

        return new MediaUploadResponse(url, "certificate");
    }

    private void validateImageFile(MultipartFile file) {
        validateFileSize(file, 5 * 1024 * 1024);
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Разрешены только изображения (jpg, png, webp)");
        }
    }

    private void validateFileSize(MultipartFile file, long maxBytes) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    "Файл слишком большой. Максимум: " + (maxBytes / 1024 / 1024) + " MB");
        }
    }
}