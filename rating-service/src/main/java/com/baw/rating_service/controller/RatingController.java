package com.baw.rating_service.controller;

import com.baw.rating_service.dto.RatingDTO;
import com.baw.rating_service.dto.RatingSummaryDTO;
import com.baw.rating_service.request.RatingRequest;
import com.baw.rating_service.service.IRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ratings")
public class RatingController {

    private final IRatingService ratingService;

    @PreAuthorize("hasRole('JUDGE')")
    @PostMapping
    public ResponseEntity<RatingDTO> submitRating(
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID judgeId = UUID.fromString(jwt.getSubject());
        RatingDTO rating = ratingService.submitRating(judgeId, request);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/{dogId}/summary")
    public ResponseEntity<RatingSummaryDTO> getSummary(@PathVariable UUID dogId) {
        return ResponseEntity.ok(ratingService.getSummary(dogId));
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<RatingSummaryDTO>> getSummaries(
            @RequestParam List<UUID> dogIds
    ) {
        return ResponseEntity.ok(ratingService.getSummaries(dogIds));
    }
}
