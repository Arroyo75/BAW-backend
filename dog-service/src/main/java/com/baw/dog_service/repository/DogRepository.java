package com.baw.dog_service.repository;

import com.baw.dog_service.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface DogRepository extends JpaRepository<Dog, UUID> {
}
