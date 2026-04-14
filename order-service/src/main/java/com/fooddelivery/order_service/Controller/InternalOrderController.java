package com.fooddelivery.order_service.Controller;

import com.fooddelivery.order_service.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderRepository orderRepository;

    @GetMapping("/client/{clientId}/top-restaurants")
    public ResponseEntity<List<UUID>> getTopRestaurants(@PathVariable UUID clientId) {
        Pageable top5 = PageRequest.of(0, 5);
        List<UUID> restaurantIds = orderRepository.findTopRestaurantIdsByClientId(clientId, top5);
        return ResponseEntity.ok(restaurantIds);
    }
}