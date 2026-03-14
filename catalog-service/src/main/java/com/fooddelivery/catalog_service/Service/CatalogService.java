package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.MenuCategoryResponse;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Enum.CuisineType;
import com.fooddelivery.catalog_service.Mapper.RestaurantMapper;
import com.fooddelivery.catalog_service.Repository.MenuCategoryRepository;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public Page<RestaurantResponse> getRestaurants(String city, CuisineType cuisineType, Pageable pageable){
        if(city != null && cuisineType != null) {
            return restaurantRepository.findByIsActiveTrueAndCity(city, pageable).map(restaurantMapper::toResponse);
        } else if (city != null) {
            return restaurantRepository.findByIsActiveTrueAndCity(city, pageable).map(restaurantMapper::toResponse);
        } else if (cuisineType != null) {
            return restaurantRepository.findByIsActiveTrueAndCuisineType(cuisineType, pageable).map(restaurantMapper::toResponse);
        }
        return restaurantRepository.findByIsActiveTrue(pageable).map(restaurantMapper::toResponse);

    }

    public RestaurantResponse getRestaurantBySlug(String slug){
        Restaurant restaurant = restaurantRepository.findBySlug(slug)
                .orElseThrow(()-> new RuntimeException("Ресторан не найден"));
        return restaurantMapper.toResponse(restaurant);
    }

    public List<MenuCategoryResponse> getMenu(UUID restaurantId){
        return menuCategoryRepository.findByRestaurantIdOrderByPosition(restaurantId)
                .stream()
                .map(restaurantMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

}
