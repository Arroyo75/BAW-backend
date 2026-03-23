package com.baw.rating_service.repository;

import com.baw.rating_service.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByDogIdAndJudgeId(UUID dogId, UUID judgeId);
    List<Rating> findAllByDogId(UUID dogId);
}
