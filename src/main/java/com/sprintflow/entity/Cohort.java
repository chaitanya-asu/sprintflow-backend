package com.sprintflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cohorts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cohort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., PY-DEV-2024-Q1

    @Column(name = "pattern_type")
    private String patternType; // Python-DevOps, Java-FullStack, etc.

    private String status; // Active, Completed, Planned

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
