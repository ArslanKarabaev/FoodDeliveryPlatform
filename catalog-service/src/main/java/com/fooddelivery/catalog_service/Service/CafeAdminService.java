package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.*;
import com.fooddelivery.catalog_service.Entity.MenuCategory;
import com.fooddelivery.catalog_service.Entity.MenuItem;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Mapper.RestaurantMapper;
import com.fooddelivery.catalog_service.Repository.MenuCategoryRepository;
import com.fooddelivery.catalog_service.Repository.MenuItemRepository;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CafeAdminService {
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResponse updateProfile(UUID cafeId, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            restaurant.setCity(request.getCity());
        }
        if (request.getLatitude() != null) {
            restaurant.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            restaurant.setLongitude(request.getLongitude());
        }
        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        if (request.getMinOrderAmount() != null) {
            restaurant.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getDeliveryZoneRadiusKm() != null) {
            restaurant.setDeliveryZoneRadiusKm(request.getDeliveryZoneRadiusKm());
        }
        if (request.getWorkingHours() != null) {
            restaurant.setWorkingHours(request.getWorkingHours());
        }

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

        item.setName(request.getName());
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

}
