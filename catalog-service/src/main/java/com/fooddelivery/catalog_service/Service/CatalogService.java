package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Client.OrderServiceClient;
import com.fooddelivery.catalog_service.Dto.MenuCategoryResponse;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import com.fooddelivery.catalog_service.Mapper.RestaurantMapper;
import com.fooddelivery.catalog_service.Repository.MenuCategoryRepository;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantMapper restaurantMapper;
    private final OrderServiceClient orderServiceClient;

    public Page<RestaurantResponse> getRestaurants(
            String city,
            CuisineType cuisineType,
            Double minRating,
            Integer maxDeliveryTime,
            Pageable pageable) {

        return restaurantRepository.findWithFilters(
                        city, cuisineType, minRating, maxDeliveryTime, pageable)
                .map(restaurantMapper::toResponse);
    }

    public RestaurantResponse getRestaurantBySlug(String slug) {
        Restaurant restaurant = restaurantRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));
        return restaurantMapper.toResponse(restaurant);
    }

    public List<MenuCategoryResponse> getMenu(UUID restaurantId) {
        return menuCategoryRepository.findByRestaurantIdWithItems(restaurantId)
                .stream()
                .map(restaurantMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<RestaurantResponse> getRecommendations(UUID clientId) {
        List<UUID> topRestaurantIds = orderServiceClient.getTopRestaurantIds(clientId);

        if (topRestaurantIds.isEmpty()) {
            return restaurantRepository
                    .findTop10ByIsActiveTrueOrderByAverageRatingDesc()
                    .stream()
                    .map(restaurantMapper::toResponse)
                    .collect(Collectors.toList());
        }

        List<CuisineType> favouriteCuisines = restaurantRepository
                .findAllById(topRestaurantIds)
                .stream()
                .map(Restaurant::getCuisineType)
                .distinct()
                .collect(Collectors.toList());

        return restaurantRepository
                .findRecommended(favouriteCuisines,
                        topRestaurantIds,
                        PageRequest.of(0, 10))
                .stream()
                .map(restaurantMapper::toResponse)
                .collect(Collectors.toList());
    }
}