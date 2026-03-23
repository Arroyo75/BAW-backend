package com.baw.rating_service.service;

import com.baw.rating_service.dto.RatingDTO;
import com.baw.rating_service.dto.RatingSummaryDTO;
import com.baw.rating_service.model.Rating;
import com.baw.rating_service.model.RatingSummary;
import com.baw.rating_service.repository.RatingRepository;
import com.baw.rating_service.repository.RatingSummaryRepository;
import com.baw.rating_service.request.RatingRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RatingService implements IRatingService {

    private final RatingRepository ratingRepository;
    private final RatingSummaryRepository summaryRepository;

    @Transactional
    @Override
    public RatingDTO submitRating(UUID judgeId, RatingRequest request) {
        Rating rating = ratingRepository
                .findByDogIdAndJudgeId(request.getDogId(), judgeId)
                .orElse(new Rating());

        rating.setDogId(request.getDogId());
        rating.setJudgeId(judgeId);
        rating.setRating(request.getRating());
        ratingRepository.save(rating);

        List<Rating> allRating = ratingRepository.findAllByDogId(request.getDogId());
        double average = allRating.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);

        RatingSummary summary = summaryRepository
                .findById(request.getDogId())
                .orElse(new RatingSummary());

        summary.setDogId(request.getDogId());
        summary.setAverage(average);
        summary.setCount(allRating.size());
        summary.setUpdatedAt(Instant.now());
        summaryRepository.save(summary);

        return convertToDTO(rating);
    }

    @Override
    public RatingSummaryDTO getSummary(UUID dogId) {
        return summaryRepository.findById(dogId)
                .map(this::convertToDTO)
                .orElse(new RatingSummaryDTO(dogId, 0.0, 0));
    }

    @Override
    public List<RatingSummaryDTO> getSummaries(List<UUID> dogIds) {
        return summaryRepository.findAllByDogIdIn(dogIds)
                .stream()
                .map(this::convertToDTO).toList();
    }

    private RatingDTO convertToDTO(Rating rating) {
        return RatingDTO.builder()
                .dogID(rating.getDogId())
                .rating(rating.getRating())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }

    private RatingSummaryDTO convertToDTO(RatingSummary summary) {
        return RatingSummaryDTO.builder()
                .dogId(summary.getDogId())
                .average(summary.getAverage())
                .count(summary.getCount())
                .build();
    }
}
