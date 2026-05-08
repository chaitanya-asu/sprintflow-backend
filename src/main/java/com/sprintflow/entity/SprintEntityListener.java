package com.sprintflow.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

public class SprintEntityListener {

    @PrePersist
    public void prePersist(Sprint sprint) {
        LocalDateTime now = LocalDateTime.now();
        if (sprint.getCreatedAt() == null) {
            sprint.setCreatedAt(now);
        }
        if (sprint.getUpdatedAt() == null) {
            sprint.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void preUpdate(Sprint sprint) {
        sprint.setUpdatedAt(LocalDateTime.now());
    }
}
