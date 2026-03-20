package com.baw.dog_service.repository;

import com.baw.dog_service.model.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface DogRepository extends JpaRepository<Dog, UUID>, JpaSpecificationExecutor<Dog> {
    Page<Dog> findByOwnerId(UUID ownerId, Pageable pageable);
    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);
}
