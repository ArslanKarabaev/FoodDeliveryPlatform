package com.fooddelivery.catalog_service.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.order.url}")
    private String orderServiceUrl;

    public List<UUID> getTopRestaurantIds(UUID clientId) {
        try {
            ResponseEntity<List<UUID>> response = restTemplate.exchange(
                    orderServiceUrl + "/internal/orders/client/" + clientId + "/top-restaurants",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UUID>>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
