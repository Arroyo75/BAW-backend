package com.baw.rating_service.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RatingRequest {

    @NotNull
    private UUID dogId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
