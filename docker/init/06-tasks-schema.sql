-- Complete Tasks Table Schema
-- This is the correct schema for the task management feature

-- Drop existing table if you want to start fresh (WARNING: This will delete all task data)
-- DROP TABLE IF EXISTS tasks;

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'NOT_SUBMITTED', 'CLOSED') NOT NULL DEFAULT 'NOT_STARTED',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    sprint_id BIGINT NULL,
    assigned_to VARCHAR(255),
    created_by VARCHAR(255),
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key to sprints table (optional)
    CONSTRAINT fk_tasks_sprint FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE SET NULL,
    
    -- Indexes for better query performance
    INDEX idx_tasks_assigned_to (assigned_to),
    INDEX idx_tasks_created_by (created_by),
    INDEX idx_tasks_sprint_id (sprint_id),
    INDEX idx_tasks_status (status),
    INDEX idx_tasks_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data for testing (optional)
-- INSERT INTO tasks (title, description, status, priority, assigned_to, created_by, due_date) VALUES
-- ('Setup development environment', 'Install all required tools and dependencies', 'NOT_STARTED', 'HIGH', 'surya.p@sprintflow.com', 'surya@sprintflow.com', '2026-05-15'),
-- ('Review sprint requirements', 'Go through all sprint requirements and clarify doubts', 'IN_PROGRESS', 'MEDIUM', 'surya.p@sprintflow.com', 'satwika@sprintflow.com', '2026-05-10'),
-- ('Prepare training materials', 'Create slides and code examples for the session', 'NOT_STARTED', 'HIGH', 'satwika@sprintflow.com', 'surya@sprintflow.com', '2026-05-12');
