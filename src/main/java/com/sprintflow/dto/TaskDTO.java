package com.sprintflow.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;  // "To Do", "In Progress", "Completed", "Blocked"
    private String priority;  // "Low", "Medium", "High", "Critical"
    private Long sprintId;
    private String sprintTitle;
    private String assignedTo;  // User email
    private String createdBy;  // User email
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}