-- ═══════════════════════════════════════════════════════════════════════════════
-- SprintFlow — CRITICAL DATABASE SCHEMA FIXES
-- Migration Script to Fix All Schema Mismatches
-- Run this AFTER schema.sql and BEFORE MASTER_SEED.sql
-- ═══════════════════════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 1: Add Missing Columns to SPRINTS Table
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE sprints
ADD COLUMN IF NOT EXISTS sprint_type VARCHAR(20) COMMENT 'TECHNOLOGY or COMMUNICATION',
ADD COLUMN IF NOT EXISTS sprint_subject VARCHAR(100) COMMENT 'Spring Boot, Django, etc.',
ADD COLUMN IF NOT EXISTS sprint_communication_type VARCHAR(100) COMMENT 'Soft Skills, Leadership, etc.',
ADD COLUMN IF NOT EXISTS deleted_at DATETIME COMMENT 'Soft delete timestamp';

-- Add index for soft delete queries
CREATE INDEX IF NOT EXISTS idx_sprints_deleted_at ON sprints(deleted_at);

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 2: Add Missing Columns to USERS Table
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE users
ADD COLUMN IF NOT EXISTS trainer_type VARCHAR(20) COMMENT 'TECHNOLOGY or COMMUNICATION',
ADD COLUMN IF NOT EXISTS trainer_technology VARCHAR(50) COMMENT 'Java, Python, Devops, etc.',
ADD COLUMN IF NOT EXISTS trainer_subject VARCHAR(100) COMMENT 'Spring Boot, Django, etc.',
ADD COLUMN IF NOT EXISTS trainer_communication_type VARCHAR(100) COMMENT 'Soft Skills, Leadership, etc.',
ADD COLUMN IF NOT EXISTS reset_token VARCHAR(255) COMMENT 'Password reset token',
ADD COLUMN IF NOT EXISTS reset_token_expiry DATETIME COMMENT 'Token expiration time';

-- Add indexes for password reset queries
CREATE INDEX IF NOT EXISTS idx_users_reset_token ON users(reset_token);

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 3: Create COHORTS Table (if not exists)
-- ══════════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS cohorts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT 'C1, C2, C3, etc.',
  pattern_type VARCHAR(50) NOT NULL COMMENT 'Java, Python, Devops, etc.',
  technology VARCHAR(20) COMMENT 'Mirror of pattern_type for compatibility',
  cohort_number VARCHAR(10) COMMENT 'Just the number part: 1, 2, 3',
  status VARCHAR(20) DEFAULT 'Active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (id),
  INDEX idx_cohorts_technology (technology),
  INDEX idx_cohorts_pattern_type (pattern_type),
  INDEX idx_cohorts_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 4: Create MASTER_DATA Table (if not exists)
-- ══════════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS master_data (
  id BIGINT NOT NULL AUTO_INCREMENT,
  category VARCHAR(50) NOT NULL COMMENT 'TECHNOLOGY, SUBJECT, COMM_TYPE',
  value VARCHAR(100) NOT NULL,
  display_order INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'Active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (id),
  UNIQUE KEY uq_master_data (category, value),
  INDEX idx_master_data_category (category),
  INDEX idx_master_data_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed master data
INSERT IGNORE INTO master_data (category, value, display_order) VALUES
-- Technologies
('TECHNOLOGY', 'Java', 1),
('TECHNOLOGY', 'Python', 2),
('TECHNOLOGY', 'Devops', 3),
('TECHNOLOGY', 'DotNet', 4),
('TECHNOLOGY', 'SalesForce', 5),

-- Subjects (for technology sprints)
('SUBJECT', 'Spring Boot', 1),
('SUBJECT', 'Django', 2),
('SUBJECT', 'Docker', 3),
('SUBJECT', 'Kubernetes', 4),
('SUBJECT', 'C# Web API', 5),
('SUBJECT', 'Apex', 6),

-- Communication Types
('COMM_TYPE', 'Soft Skills', 1),
('COMM_TYPE', 'Leadership', 2),
('COMM_TYPE', 'Presentation', 3),
('COMM_TYPE', 'Team Building', 4),
('COMM_TYPE', 'Conflict Resolution', 5);

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 5: Add Enhanced Columns to ATTENDANCE Table
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE attendance
ADD COLUMN IF NOT EXISTS check_out_time VARCHAR(20) COMMENT 'Check-out time e.g. 05:00 PM',
ADD COLUMN IF NOT EXISTS duration_minutes INT COMMENT 'Session duration in minutes',
ADD COLUMN IF NOT EXISTS location VARCHAR(255) COMMENT 'Physical location or IP-based location',
ADD COLUMN IF NOT EXISTS device_info VARCHAR(255) COMMENT 'Device used for check-in',
ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45) COMMENT 'IP address of check-in',
ADD COLUMN IF NOT EXISTS verified_by BIGINT COMMENT 'User who verified attendance',
ADD COLUMN IF NOT EXISTS verification_timestamp DATETIME COMMENT 'When attendance was verified';

-- Add foreign key for verifier
ALTER TABLE attendance
ADD CONSTRAINT IF NOT EXISTS fk_att_verifier 
FOREIGN KEY (verified_by) REFERENCES users(id) ON DELETE SET NULL;

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 6: Add Missing Columns to ROOMS Table
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE rooms
ADD COLUMN IF NOT EXISTS location VARCHAR(100) COMMENT 'Physical location of room',
ADD COLUMN IF NOT EXISTS floor VARCHAR(20) COMMENT 'Floor number',
ADD COLUMN IF NOT EXISTS building VARCHAR(50) COMMENT 'Building name',
ADD COLUMN IF NOT EXISTS amenities TEXT COMMENT 'Room amenities (projector, whiteboard, etc.)';

-- Update existing rooms to extract location from name
UPDATE rooms 
SET location = SUBSTRING_INDEX(name, ' - ', -1) 
WHERE name LIKE '% - %' AND (location IS NULL OR location = '');

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 7: Add Performance Indexes
-- ══════════════════════════════════════════════════════════════════════════════

-- Sprints indexes
CREATE INDEX IF NOT EXISTS idx_sprints_trainer_status ON sprints(trainer_id, status);
CREATE INDEX IF NOT EXISTS idx_sprints_date_range ON sprints(start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_sprints_technology ON sprints(technology);

-- Employees indexes
CREATE INDEX IF NOT EXISTS idx_employees_tech_cohort ON employees(technology, cohort);
CREATE INDEX IF NOT EXISTS idx_employees_status ON employees(status);

-- Attendance indexes (composite for common queries)
CREATE INDEX IF NOT EXISTS idx_attendance_sprint_date ON attendance(sprint_id, attendance_date);
CREATE INDEX IF NOT EXISTS idx_attendance_employee_date ON attendance(employee_id, attendance_date);
CREATE INDEX IF NOT EXISTS idx_attendance_status ON attendance(status);

-- Sprint employees indexes
CREATE INDEX IF NOT EXISTS idx_sprint_employees_status ON sprint_employees(status);

-- Notifications indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_read_created ON notifications(user_email, is_read, created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_category ON notifications(category);

-- Audit logs indexes
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 8: Add Missing Constraints
-- ══════════════════════════════════════════════════════════════════════════════

-- Ensure email uniqueness is enforced
ALTER TABLE employees 
MODIFY COLUMN email VARCHAR(150) UNIQUE;

-- Ensure status values are valid
ALTER TABLE sprints 
ADD CONSTRAINT IF NOT EXISTS chk_sprint_status 
CHECK (status IN ('Scheduled', 'On Hold', 'Completed'));

ALTER TABLE employees 
ADD CONSTRAINT IF NOT EXISTS chk_employee_status 
CHECK (status IN ('Active', 'Inactive'));

ALTER TABLE attendance 
ADD CONSTRAINT IF NOT EXISTS chk_attendance_status 
CHECK (status IN ('Present', 'Late', 'Absent'));

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 9: Update Existing Data for Compatibility
-- ══════════════════════════════════════════════════════════════════════════════

-- Set technology field in cohorts table (mirror of pattern_type)
UPDATE cohorts 
SET technology = pattern_type 
WHERE technology IS NULL OR technology = '';

-- Ensure all sprints have a valid status
UPDATE sprints 
SET status = 'Scheduled' 
WHERE status IS NULL OR status = '';

-- Ensure all employees have a valid status
UPDATE employees 
SET status = 'Active' 
WHERE status IS NULL OR status = '';

-- ══════════════════════════════════════════════════════════════════════════════
-- FIX 10: Add Audit Trail Triggers (Optional but Recommended)
-- ══════════════════════════════════════════════════════════════════════════════

DELIMITER $$

-- Trigger for sprint updates
CREATE TRIGGER IF NOT EXISTS trg_sprints_audit_update
AFTER UPDATE ON sprints
FOR EACH ROW
BEGIN
  IF OLD.status != NEW.status THEN
    INSERT INTO audit_logs (action, performed_by, details, entity_name, entity_id, created_at)
    VALUES (
      'SPRINT_STATUS_CHANGE',
      COALESCE((SELECT email FROM users WHERE id = NEW.trainer_id), 'SYSTEM'),
      CONCAT('Status changed from ', OLD.status, ' to ', NEW.status),
      'SPRINT',
      NEW.id,
      NOW()
    );
  END IF;
END$$

-- Trigger for attendance submission
CREATE TRIGGER IF NOT EXISTS trg_attendance_audit_submit
AFTER UPDATE ON attendance
FOR EACH ROW
BEGIN
  IF OLD.submitted = 0 AND NEW.submitted = 1 THEN
    INSERT INTO audit_logs (action, performed_by, details, entity_name, entity_id, created_at)
    VALUES (
      'ATTENDANCE_SUBMITTED',
      COALESCE((SELECT email FROM users WHERE id = NEW.marked_by), 'SYSTEM'),
      CONCAT('Attendance submitted for date ', NEW.attendance_date),
      'ATTENDANCE',
      NEW.id,
      NOW()
    );
  END IF;
END$$

DELIMITER ;

-- ══════════════════════════════════════════════════════════════════════════════
-- VERIFICATION QUERIES
-- ══════════════════════════════════════════════════════════════════════════════

-- Verify all tables exist
SELECT 
  'Tables Check' AS verification,
  COUNT(*) AS table_count
FROM information_schema.tables 
WHERE table_schema = 'sprintflow_db' 
AND table_name IN ('users', 'employees', 'sprints', 'sprint_employees', 'attendance', 
                   'rooms', 'notifications', 'audit_logs', 'tasks', 'cohorts', 'master_data',
                   'chat_messages', 'chat_groups', 'chat_group_members', 'messages');

-- Verify sprint columns
SELECT 
  'Sprint Columns' AS verification,
  COUNT(*) AS column_count
FROM information_schema.columns 
WHERE table_schema = 'sprintflow_db' 
AND table_name = 'sprints'
AND column_name IN ('sprint_type', 'sprint_subject', 'sprint_communication_type', 'deleted_at');

-- Verify user columns
SELECT 
  'User Columns' AS verification,
  COUNT(*) AS column_count
FROM information_schema.columns 
WHERE table_schema = 'sprintflow_db' 
AND table_name = 'users'
AND column_name IN ('trainer_type', 'trainer_technology', 'trainer_subject', 
                    'trainer_communication_type', 'reset_token', 'reset_token_expiry');

-- Verify indexes
SELECT 
  'Indexes Check' AS verification,
  COUNT(*) AS index_count
FROM information_schema.statistics 
WHERE table_schema = 'sprintflow_db'
AND index_name LIKE 'idx_%';

-- ══════════════════════════════════════════════════════════════════════════════
-- COMPLETION MESSAGE
-- ══════════════════════════════════════════════════════════════════════════════

SELECT '✅ Database schema fixes completed successfully!' AS status;
SELECT 'Next step: Run MASTER_SEED.sql to populate data' AS next_action;
