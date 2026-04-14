package com.fooddelivery.delivery_service.Client;

import com.fooddelivery.delivery_service.Dto.YandexClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexDeliveryClient {
    private final RestTemplate restTemplate;

    @Value("${yandex.delivery.api.url}")
    private String apiUrl;

    @Value("${yandex.delivery.api.key}")
    private String apiKey;

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept-Language", "ru");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public YandexClientDto.ClaimResponse createClaim(YandexClientDto.ClaimRequest request) {
        String requestId = UUID.randomUUID().toString();
        String url = apiUrl + "/b2b/cargo/integration/v2/claims/create?request_id=" + requestId;

        HttpEntity<YandexClientDto.ClaimRequest> entity = new HttpEntity<>(request, buildHeaders());

        log.info("Создание заявки Яндекс Доставки, request_id: {}", requestId);
        ResponseEntity<YandexClientDto.ClaimResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, YandexClientDto.ClaimResponse.class
        );
        return response.getBody();
    }

    public YandexClientDto.ClaimResponse acceptClaim(String claimId, Integer version) {
        String url = apiUrl + "/b2b/cargo/integration/v2/claims/accept?claim_id=" + claimId;

        Map<String, Integer> body = Map.of("version", version);
        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(body, buildHeaders());

        log.info("Подтверждение заявки: {}", claimId);
        ResponseEntity<YandexClientDto.ClaimResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, YandexClientDto.ClaimResponse.class
        );
        return response.getBody();
    }

    public YandexClientDto.ClaimResponse getClaimInfo(String claimId) {
        String url = apiUrl + "/b2b/cargo/integration/v2/claims/info?claim_id=" + claimId;

        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<YandexClientDto.ClaimResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, YandexClientDto.ClaimResponse.class
        );
        return response.getBody();
    }

    public String getClaimStatus(String claimId) {
        YandexClientDto.ClaimResponse info = getClaimInfo(claimId);
        return info.getStatus();
    }
}