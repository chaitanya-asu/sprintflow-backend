-- SprintFlow — Seed Data
-- Creates a default MANAGER user so you can log in immediately after deployment.
-- Password: Admin@123  (change this after first login via Profile page)

USE sprintflow_db;

-- Default manager (password hash for "Admin@123" using BCrypt strength 10)
INSERT INTO users (name, email, password, role, department, status, joined_date, password_changed)
VALUES (
  'System Admin',
  'admin@sprintflow.local',
  '$2a$10$8K1p/a0dL2LkUnPV9/zrLOCIjqGOJ9NkdTY.WT08ibRczoxwOmB6W',
  'MANAGER',
  'Administration',
  'Active',
  CURDATE(),
  0
)
ON DUPLICATE KEY UPDATE id=id;

-- You can add sample employees, sprints, etc. here if needed for demo purposes
