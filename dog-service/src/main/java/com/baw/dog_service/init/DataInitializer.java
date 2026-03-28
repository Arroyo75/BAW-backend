package com.baw.dog_service.init;

import com.baw.dog_service.client.DogApiClient;
import com.baw.dog_service.model.Dog;
import com.baw.dog_service.repository.DogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer implements ApplicationRunner {

    private final DogRepository dogRepository;
    private final DogApiClient apiClient;

    @Value("${app.seed.owner-ids}")
    private String importedOwnerIds;

    private List<UUID> ownerIds;

    @PostConstruct
    public void init() {
        ownerIds = Arrays.stream(importedOwnerIds.split(","))
                .map(String::trim)
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(dogRepository.count() > 0) {
            log.info("Dogs already seeded, skipping init");
            return;
        }

        log.info("Starting init");

        List<String> breeds = apiClient.fetchBreeds();
        if(breeds.isEmpty()) {
            log.warn("No breeds fetched, skipping seeding");
            return;
        }

        List<Dog> dogs = new ArrayList<>();
        int ownerIndex = 0;
        UUID ownerId = null;

        for(String breed : breeds) {
            String imageUrl = apiClient.fetchImageForBreed(breed).orElse(null);

            ownerIndex = ownerIndex % ownerIds.size();

            Dog dog = Dog.builder()
                    .nickname(uped(breed))
                    .breed(breed)
                    .age(2)
                    .description("Imported")
                    .image(imageUrl)
                    .ownerId(ownerIds.get(ownerIndex % ownerIds.size()))
                    .build();

            dogs.add(dog);

            ownerIndex++;
            log.info("Prepared");
        }

        dogRepository.saveAll(dogs);
        log.info("Finished init");
    }

    private String uped(String s) {
        if(s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
