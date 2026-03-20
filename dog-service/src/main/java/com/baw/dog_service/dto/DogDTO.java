package com.baw.dog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class DogDTO {
    private UUID id;
    private UUID ownerId;
    private String nickname;
    private String breed;
    private Integer age;
    private String image;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
