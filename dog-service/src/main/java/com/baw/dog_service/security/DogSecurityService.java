package com.baw.dog_service.security;

import com.baw.dog_service.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component("dogSecurity")
public class DogSecurityService {

    private final DogRepository dogRepository;

    public boolean isOwner(UUID dogId, String userId) {
        return dogRepository.existsByIdAndOwnerId(dogId, UUID.fromString(userId));
    }
}
