package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Dto.CreateReviewRequest;
import com.fooddelivery.catalog_service.Entity.Restaurant;
import com.fooddelivery.catalog_service.Entity.Review;
import com.fooddelivery.catalog_service.Repository.RestaurantRepository;
import com.fooddelivery.catalog_service.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    public void addReview(CreateReviewRequest request, UUID clientId) {
        if (request.getMenuItemId() != null) {
            addMenuItemReview(request, clientId);
        } else {
            addRestaurantReview(request, clientId);
        }
    }

    private void addRestaurantReview(CreateReviewRequest request, UUID clientId) {
        if (reviewRepository.existsByClientIdAndRestaurantId(clientId, request.getRestaurantId())) {
            throw new RuntimeException("Вы уже оставили отзыв на это заведение");
        }

        validateRating(request.getRating());

        Review review = Review.builder()
                .clientId(clientId)
                .restaurantId(request.getRestaurantId())
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        recalculateRestaurantRating(request.getRestaurantId());
    }

    private void addMenuItemReview(CreateReviewRequest request, UUID clientId) {
        if (reviewRepository.existsByClientIdAndMenuItemIdAndOrderId(
                clientId, request.getMenuItemId(), request.getOrderId())) {
            throw new RuntimeException("Вы уже оставили отзыв на это блюдо");
        }

        validateRating(request.getRating());

        Review review = Review.builder()
                .clientId(clientId)
                .restaurantId(request.getRestaurantId())
                .orderId(request.getOrderId())
                .menuItemId(request.getMenuItemId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
    }

    private void recalculateRestaurantRating(UUID restaurantId) {
        Double avg = reviewRepository.calculateAverageRating(restaurantId);
        Integer count = reviewRepository.countByRestaurantId(restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        restaurant.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        restaurant.setReviewCount(count);
        restaurantRepository.save(restaurant);
    }

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 5");
        }
    }

    public List<Review> getRestaurantReviews(UUID restaurantId) {
        return reviewRepository.findByRestaurantIdAndMenuItemIdIsNullOrderByCreatedAtDesc(restaurantId);
    }

    public List<Review> getMenuItemReviews(UUID menuItemId) {
        return reviewRepository.findByMenuItemIdOrderByCreatedAtDesc(menuItemId);
    }
}