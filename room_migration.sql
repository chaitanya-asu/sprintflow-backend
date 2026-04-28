-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Room Data Migration
-- Adds initial room data to the rooms table
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- Insert initial rooms
INSERT INTO rooms (name, capacity, status, created_at, updated_at) VALUES
('Room A - Sandeepa', 30, 'Active', NOW(), NOW()),
('Room B - Dhrona', 25, 'Active', NOW(), NOW()),
('Room C - Brahma', 20, 'Active', NOW(), NOW()),
('Room D - Maheshwara', 35, 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  capacity = VALUES(capacity),
  status = VALUES(status),
  updated_at = NOW();

-- Verify insertion
SELECT * FROM rooms;
