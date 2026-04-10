package com.fooddelivery.order_service.Client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogServiceClient {
    private final RestTemplate restTemplate;

    @Value("${services.catalog.url}")
    private String catalogServiceUrl;

    @Data
    public static class RestaurantInfo {
        private String name;
        private String phone;
        private String address;
        private Double latitude;
        private Double longitude;
    }

    public RestaurantInfo getRestaurantInfo(UUID restaurantId) {
        try {
            String url = catalogServiceUrl
                    + "/internal/restaurants/" + restaurantId + "/contact";
            return restTemplate.getForObject(url, RestaurantInfo.class);
        } catch (Exception e) {
            log.error("Не удалось получить данные ресторана {}: {}",
                    restaurantId, e.getMessage());
            throw new RuntimeException("Ошибка получения данных ресторана");
        }
    }


}
