package com.sprintflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SprintDTO {

    private Long id;
    private String title;           // was "name"
    private String technology;
    private String cohort;          // primary cohort
    private List<CohortPair> cohorts; // multi tech+cohort pairs
    private Long trainerId;
    private String trainer;         // trainer name string (frontend uses this)
    private String room;            // was "location"
    private LocalDate startDate;
    private LocalDate endDate;
    private String sprintStart;     // e.g. 09:00 AM
    private String sprintEnd;       // e.g. 05:00 PM
    private String timeSlot;        // computed: sprintStart - sprintEnd
    private String status;          // Scheduled, On Hold, Completed
    private String instructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested cohort pair matching frontend shape
    public static class CohortPair {
        private String technology;
        private String cohort;

        public CohortPair() {}
        public CohortPair(String technology, String cohort) {
            this.technology = technology;
            this.cohort = cohort;
        }

        public String getTechnology() { return technology; }
        public void setTechnology(String technology) { this.technology = technology; }

        public String getCohort() { return cohort; }
        public void setCohort(String cohort) { this.cohort = cohort; }
    }

    public SprintDTO() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public List<CohortPair> getCohorts() { return cohorts; }
    public void setCohorts(List<CohortPair> cohorts) { this.cohorts = cohorts; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }

    public String getTrainer() { return trainer; }
    public void setTrainer(String trainer) { this.trainer = trainer; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getSprintStart() { return sprintStart; }
    public void setSprintStart(String sprintStart) { this.sprintStart = sprintStart; }

    public String getSprintEnd() { return sprintEnd; }
    public void setSprintEnd(String sprintEnd) { this.sprintEnd = sprintEnd; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
