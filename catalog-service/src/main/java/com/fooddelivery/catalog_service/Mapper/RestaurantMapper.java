package com.fooddelivery.catalog_service.Mapper;

import com.fooddelivery.catalog_service.Dto.MenuCategoryResponse;
import com.fooddelivery.catalog_service.Dto.MenuItemResponse;
import com.fooddelivery.catalog_service.Dto.RestaurantResponse;
import com.fooddelivery.catalog_service.Entity.MenuCategory;
import com.fooddelivery.catalog_service.Entity.MenuItem;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    RestaurantResponse toResponse(Restaurant restaurant);
    @Mapping(target = "items", source = "items")
    MenuCategoryResponse toCategoryResponse(MenuCategory category);
    MenuItemResponse toItemResponse(MenuItem item);
}
