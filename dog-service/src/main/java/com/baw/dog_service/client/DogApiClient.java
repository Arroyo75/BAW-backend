package com.baw.dog_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DogApiClient {

    private static final String API_BASE = "https://dog.ceo/api";

    private final RestClient restClient;

    public DogApiClient() {
        this.restClient = RestClient.create();
    }

    public List<String> fetchBreeds() {

        try {
            Map<String, Object> response = restClient.get()
                    .uri(API_BASE + "/breeds/list/all")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if(response == null || !"success".equals(response.get("status"))) {
                log.warn("API returned unexpected response");
                return List.of();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) response.get("message");

            return message.keySet().stream()
                    .limit(44)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to fetch breeds: {}", e.getMessage());
            return List.of();
        }
    }

    public Optional<String> fetchImageForBreed(String breed) {

        try {
            String normalizeBreed = breed.toLowerCase().trim();

            Map<String, String> response = restClient.get()
                    .uri(API_BASE + "/breed/" + normalizeBreed + "/images/random")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if(response != null && "success".equals(response.get("status"))) {
                return Optional.ofNullable(response.get("message"));
            }
        } catch (Exception e) {
            log.warn("Failed to fetch image for breed {}: {}", breed, e.getMessage());
        }

        return Optional.empty();
    }



}
