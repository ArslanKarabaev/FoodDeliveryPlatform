package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.CreatePromotionRequest;
import com.fooddelivery.catalog_service.Entity.MenuItem;
import com.fooddelivery.catalog_service.Entity.Promotion;
import com.fooddelivery.catalog_service.Repository.MenuItemRepository;
import com.fooddelivery.catalog_service.Repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final MenuItemRepository menuItemRepository;

    public Promotion createPromotion(CreatePromotionRequest request, UUID restaurantId) {
        MenuItem item = menuItemRepository.findByIdWithCategoryAndRestaurant(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));

        if (!item.getCategory().getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Блюдо не принадлежит вашему ресторану");
        }

        if (promotionRepository.existsByMenuItemIdAndIsActiveTrue(request.getMenuItemId())) {
            throw new RuntimeException("На это блюдо уже есть активная акция");
        }

        BigDecimal originalPrice = item.getPrice();
        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - request.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));
        BigDecimal discountedPrice = originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        Promotion promotion = Promotion.builder()
                .restaurantId(restaurantId)
                .menuItemId(request.getMenuItemId())
                .discountPercent(request.getDiscountPercent())
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .isActive(true)
                .build();

        return promotionRepository.save(promotion);
    }

    public void deactivatePromotion(UUID promotionId, UUID restaurantId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Акция не найдена"));

        if (!promotion.getRestaurantId().equals(restaurantId)) {
            throw new RuntimeException("Нет доступа к этой акции");
        }

        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    public List<Promotion> getAllActivePromotions() {
        return promotionRepository.findAllActive(LocalDateTime.now());
    }

    public List<UUID> getRestaurantIdsWithPromotions() {
        return promotionRepository.findRestaurantIdsWithActivePromotions(LocalDateTime.now());
    }

    public List<Promotion> getRestaurantPromotions(UUID restaurantId) {
        return promotionRepository.findByRestaurantIdAndIsActiveIsTrue(restaurantId);
    }
}