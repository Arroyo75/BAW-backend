package com.baw.rating_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ratings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"dog_id", "judge_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "judge_id", nullable = false)
    private UUID judgeId;

    @Column(name = "dog_id", nullable = false)
    private UUID dogId;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }
}
