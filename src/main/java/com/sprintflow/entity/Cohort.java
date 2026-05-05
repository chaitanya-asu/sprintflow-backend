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
    private String name; // e.g., "Java C1" (auto-generated from technology + cohort_number)

    @Column(name = "pattern_type")
    private String patternType; // Java, Python, Devops, DotNet, SalesForce

    @Column(name = "technology", length = 20)
    private String technology; // Java, Python, Devops, DotNet, SalesForce

    @Column(name = "cohort_number", length = 10)
    private String cohortNumber; // C1, C2, C3, etc.

    private String status; // Active, Completed, Planned

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Auto-generate name from technology and cohort_number if not provided
        if ((name == null || name.isBlank()) && technology != null && cohortNumber != null) {
            this.name = technology + " " + cohortNumber;
        }
        // Ensure patternType matches technology
        if (patternType == null && technology != null) {
            this.patternType = technology;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Keep name in sync with technology and cohort_number
        if (technology != null && cohortNumber != null) {
            this.name = technology + " " + cohortNumber;
        }
        // Ensure patternType matches technology
        if (technology != null) {
            this.patternType = technology;
        }
    }
}
