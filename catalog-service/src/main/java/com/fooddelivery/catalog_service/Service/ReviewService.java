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

    public void addReview(CreateReviewRequest request, UUID clientId){
        if(reviewRepository.existsByClientIdAndRestaurantId(clientId,request.getRestaurantId())){
            throw new RuntimeException("Вы уже оставили отзыв на это заведение");
        }

        if (request.getRating() < 1 || request.getRating() > 5){
            throw new RuntimeException("Рейтинг должен быть от 1 до 5");
        }

        Review review = Review.builder()
                .clientId(clientId)
                .restaurantId(request.getRestaurantId())
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);

        recalculateRating(request.getRestaurantId());
    }

    private void recalculateRating(UUID restaurantId) {
        Double avg = reviewRepository.calculateAverageRating(restaurantId);
        Integer count = reviewRepository.countByRestaurantId(restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        restaurant.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        restaurant.setReviewCount(count);
        restaurantRepository.save(restaurant);
    }

    public List<Review> getReviews(UUID restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

}
