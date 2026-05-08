-- Migration script to update tasks table for email-based assignment
-- Run this script to update your existing tasks table

-- Step 1: Add new columns for email-based assignment
ALTER TABLE tasks 
ADD COLUMN IF NOT EXISTS assigned_to VARCHAR(255),
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

-- Step 2: Migrate existing data from assigned_to_id to assigned_to (if you have existing data)
-- This assumes you have a users table with email and employee relationship
-- UPDATE tasks t
-- SET assigned_to = (SELECT u.email FROM users u 
--                    INNER JOIN employees e ON u.id = e.user_id 
--                    WHERE e.id = t.assigned_to_id)
-- WHERE t.assigned_to_id IS NOT NULL;

-- Step 3: Drop the old foreign key constraint and column (after data migration)
-- ALTER TABLE tasks DROP FOREIGN KEY IF EXISTS fk_tasks_assigned_to;
-- ALTER TABLE tasks DROP COLUMN IF EXISTS assigned_to_id;

-- Step 4: Update status enum values to match frontend expectations
-- Note: This depends on your MySQL version and existing data
-- You may need to update existing records first before changing the enum

-- Step 5: Make sprint_id nullable (tasks can exist without sprint)
ALTER TABLE tasks 
MODIFY COLUMN sprint_id BIGINT NULL;

-- Step 6: Remove unused columns (if they exist)
ALTER TABLE tasks 
DROP COLUMN IF EXISTS story_point,
DROP COLUMN IF EXISTS estimated_hours,
DROP COLUMN IF EXISTS actual_hours;

-- Step 7: Update due_date to DATE type instead of DATETIME
ALTER TABLE tasks 
MODIFY COLUMN due_date DATE;

-- Verify the changes
DESCRIBE tasks;
