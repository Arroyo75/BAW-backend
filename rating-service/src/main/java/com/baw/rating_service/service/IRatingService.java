package com.baw.rating_service.service;

import com.baw.rating_service.dto.RatingDTO;
import com.baw.rating_service.dto.RatingSummaryDTO;
import com.baw.rating_service.request.RatingRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRatingService {
    RatingDTO submitRating(UUID judgeId, RatingRequest request);
    RatingSummaryDTO getSummary(UUID dogId);
    List<RatingSummaryDTO> getSummaries(List<UUID> dogIds);
}
