package com.sprintflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TO_DO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Column(name = "assigned_to")
    private String assignedTo;  // User email

    @Column(name = "created_by")
    private String createdBy;  // User email

    @Column(name = "story_point")
    @Builder.Default
    private Integer storyPoint = 0;

    @Column(name = "estimated_hours")
    @Builder.Default
    private Double estimatedHours = 0.0;

    @Column(name = "actual_hours")
    @Builder.Default
    private Double actualHours = 0.0;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TaskStatus {
        TO_DO("To Do"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        BLOCKED("Blocked");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static TaskStatus fromDisplayName(String displayName) {
            for (TaskStatus status : values()) {
                if (status.displayName.equals(displayName)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + displayName);
        }
    }

    public enum TaskPriority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        CRITICAL("Critical");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static TaskPriority fromDisplayName(String displayName) {
            for (TaskPriority priority : values()) {
                if (priority.displayName.equals(displayName)) {
                    return priority;
                }
            }
            throw new IllegalArgumentException("Invalid priority: " + displayName);
        }
    }
}
