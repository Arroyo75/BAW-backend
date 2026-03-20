package com.baw.dog_service.service;

import com.baw.dog_service.dto.DogDTO;
import com.baw.dog_service.request.CreateDogRequest;
import com.baw.dog_service.request.DogFilterRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IDogService {

    DogDTO getDogById(UUID id);

    Page<DogDTO> getDogs(DogFilterRequest filter);
    Page<DogDTO> getDogsByOwnerId(UUID ownerId, int page, int size);

    DogDTO createDog(CreateDogRequest request, UUID ownerId);

    DogDTO updateDog(CreateDogRequest request, UUID id);

    void deleteDog(UUID id);
}
