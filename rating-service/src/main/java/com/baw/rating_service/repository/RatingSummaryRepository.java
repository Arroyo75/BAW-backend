package com.baw.rating_service.repository;

import com.baw.rating_service.model.RatingSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatingSummaryRepository extends JpaRepository<RatingSummary, UUID> {
    List<RatingSummary> findAllByDogIdIn(List<UUID> dogIds);
}
