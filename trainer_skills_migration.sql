-- Trainer Skills Migration
-- Adds trainer type, technology, subject, and communication type fields

-- Add columns to users table for trainer skills
ALTER TABLE users ADD COLUMN trainer_type VARCHAR(20) COMMENT 'TECHNOLOGY or COMMUNICATION';
ALTER TABLE users ADD COLUMN trainer_technology VARCHAR(50) COMMENT 'Java, Python, Devops, DotNet, SalesForce';
ALTER TABLE users ADD COLUMN trainer_subject VARCHAR(100) COMMENT 'Spring Boot, Django, Docker, etc.';
ALTER TABLE users ADD COLUMN trainer_communication_type VARCHAR(100) COMMENT 'Soft Skills, Leadership, Presentation, etc.';

-- Add columns to sprints table for sprint type
ALTER TABLE sprints ADD COLUMN sprint_type VARCHAR(20) COMMENT 'TECHNOLOGY or COMMUNICATION';
ALTER TABLE sprints ADD COLUMN sprint_subject VARCHAR(100) COMMENT 'Spring Boot, Django, etc.';
ALTER TABLE sprints ADD COLUMN sprint_communication_type VARCHAR(100) COMMENT 'Soft Skills, Leadership, etc.';

-- Create index for trainer filtering
CREATE INDEX idx_trainer_type_technology ON users(trainer_type, trainer_technology) WHERE role = 'TRAINER';
