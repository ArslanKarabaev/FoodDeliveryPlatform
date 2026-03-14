package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.CreateRestaurantRequest;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Mapper.RestaurantMapper;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCatalogService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {

        String slug = generateSlug(request.getName());

        if (restaurantRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .cuisineType(request.getCuisineType())
                .phone(request.getPhone())
                .email(request.getEmail())
                .minOrderAmount(request.getMinOrderAmount())
                .deliveryZoneRadiusKm(request.getDeliveryZoneRadiusKm())
                .cafeAdminId(request.getCafeAdminId())
                .isActive(false)
                .isVerified(false)
                .commissionRate(BigDecimal.valueOf(12.00))
                .build();

        return restaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public void updateRestaurantStatus(UUID id, boolean active) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));
        restaurant.setActive(active);
        restaurantRepository.save(restaurant);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
    }
}