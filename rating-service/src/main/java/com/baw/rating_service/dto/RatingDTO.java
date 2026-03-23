package com.baw.rating_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class RatingDTO {
    private UUID dogID;
    private Integer rating;
    private Instant createdAt;
    private Instant updatedAt;
}
