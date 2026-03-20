package com.baw.dog_service.service;

import com.baw.dog_service.dto.DogDTO;
import com.baw.dog_service.exception.ResourceNotFoundException;
import com.baw.dog_service.model.Dog;
import com.baw.dog_service.repository.DogRepository;
import com.baw.dog_service.request.CreateDogRequest;
import com.baw.dog_service.request.DogFilterRequest;
import com.baw.dog_service.util.DogSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class DogService implements IDogService {

    private final DogRepository dogRepository;

    @Override
    public DogDTO getDogById(UUID id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found."));

        return convertToDTO(dog);
    }

    @Override
    public Page<DogDTO> getDogs(DogFilterRequest filter) {
        Specification<Dog> spec = Specification
                .where(DogSpecification.matchesSearch(filter.getSearch()));

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("createdAt").descending());
        return dogRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<DogDTO> getDogsByOwnerId(UUID ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return dogRepository.findByOwnerId(ownerId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public DogDTO createDog(CreateDogRequest request, UUID ownerId) {

        Dog dog = Dog.builder()
                .ownerId(ownerId)
                .nickname(request.getNickname())
                .breed(request.getBreed())
                .age(request.getAge())
                .image(request.getImage())
                .description(request.getDescription())
                .build();

        Dog savedDog = dogRepository.save(dog);
        log.info("Dog created successfully");

        return convertToDTO(dog);
    }

    @Override
    public DogDTO updateDog(CreateDogRequest request, UUID id) {

        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found."));

        if(request.getNickname() != null) {
            dog.setNickname(request.getNickname());
        }

        if(request.getBreed() != null) {
            dog.setBreed(request.getBreed());
        }

        if(request.getAge() != null) {
            dog.setAge(request.getAge());
        }

        if(request.getImage() != null) {
            dog.setImage(request.getImage());
        }

        if(request.getDescription() != null) {
            dog.setDescription(request.getDescription());
        }

        Dog updatedDog = dogRepository.save(dog);
        log.info("Dog updated successfully.");

        return convertToDTO(updatedDog);
    }

    @Override
    public void deleteDog(UUID id) {
        if(!dogRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        dogRepository.deleteById(id);
        log.info("Dog successfully deleted");
    }

    private DogDTO convertToDTO(Dog dog) {
        return DogDTO.builder()
                .id(dog.getId())
                .ownerId(dog.getOwnerId())
                .nickname(dog.getNickname())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .image(dog.getImage())
                .description(dog.getDescription())
                .createdAt(dog.getCreatedAt())
                .updatedAt(dog.getUpdatedAt())
                .build();
    }
}
