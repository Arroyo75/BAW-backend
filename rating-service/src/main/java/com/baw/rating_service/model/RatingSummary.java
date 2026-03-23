package com.baw.rating_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rating_summaries")
public class RatingSummary {
    @Id
    @Column(name = "dog_id")
    private UUID dogId;

    @Column(nullable = false)
    private Double average;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
