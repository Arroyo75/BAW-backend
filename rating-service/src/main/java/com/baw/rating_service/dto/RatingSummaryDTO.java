package com.baw.rating_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class RatingSummaryDTO {
    UUID dogId;
    Double average;
    Integer count;
}
