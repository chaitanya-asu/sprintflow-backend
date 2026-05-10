-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Updated Seed Script (Standardized Cohorts)
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── 1. USERS ─────────────────────────────────────────────────────
-- Passwords reset by DataInitializer on startup to Admin@123

INSERT INTO users (name, email, password, role, phone, department, trainer_role, status, joined_date, password_changed, created_at, updated_at)
VALUES
  -- Managers
  ('Surya Prakash',  'surya@sprintflow.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000001', 'Management',    NULL,                 'Active', '2020-01-01', true, NOW(), NOW()),
  ('Aswini Pasam',   'a.pasam@ajacs.in',            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000002', 'Management',    NULL,                 'Active', '2020-01-01', true, NOW(), NOW()),
  -- HR
  ('Satwika',        's.lakkampally@ajacs.in',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000003', 'HR',            NULL,                 'Active', '2021-06-01', true, NOW(), NOW()),
  ('Nikitha',        'nikitha@ajacs.in',             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000004', 'HR',            NULL,                 'Active', '2021-06-01', true, NOW(), NOW()),
  -- Trainer (used across all sprints; department=Communication shows in all sprint types)
  ('Surya Posanapally', 's.posanapally@ajacs.in',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000005', 'Communication', 'Manager-Trainings',  'Active', '2020-07-10', true, NOW(), NOW()),
  -- Technology Trainers (department matches technology for HR sprint creation filtering)
  ('Ravi Kumar',     'ravi.kumar@ajacs.in',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000006', 'Java',        'Manager-Trainings',  'Active', '2020-08-01', true, NOW(), NOW()),
  ('Anil Sharma',    'anil.sharma@ajacs.in',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000007', 'Python',      'Manager-Trainings',  'Active', '2020-09-01', true, NOW(), NOW()),
  ('Priya Patel',    'priya.patel@ajacs.in',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000008', 'Devops',      'Manager-Trainings',  'Active', '2021-01-15', true, NOW(), NOW()),
  ('Deepika Singh',  'deepika.singh@ajacs.in',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000009', 'DotNet',      'Manager-Trainings',  'Active', '2021-02-01', true, NOW(), NOW()),
  ('Arun Verma',     'arun.verma@ajacs.in',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000010', 'SalesForce',  'Manager-Trainings',  'Active', '2021-03-01', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name       = VALUES(name),
  role       = VALUES(role),
  department = VALUES(department),
  trainer_role = VALUES(trainer_role),
  updated_at = NOW();

-- ── 1a. COHORTS ──────────────────────────────────────────────────

INSERT INTO cohorts (name, pattern_type, technology, cohort_number, status, created_at) VALUES
  ('C1', 'Java', 'Java', '1', 'Active', NOW()),
  ('C2', 'Java', 'Java', '2', 'Active', NOW()),
  ('C3', 'Python', 'Python', '3', 'Active', NOW()),
  ('C4', 'Devops', 'Devops', '4', 'Active', NOW()),
  ('C5', 'DotNet', 'DotNet', '5', 'Active', NOW()),
  ('C6', 'SalesForce', 'SalesForce', '6', 'Active', NOW())
ON DUPLICATE KEY UPDATE status = 'Active';

-- ── 2. ROOMS ─────────────────────────────────────────────────────

INSERT INTO rooms (name, capacity, status, created_at, updated_at) VALUES
  ('Room A - Sandeepa',      25, 'Active', NOW(), NOW()),
  ('Room B - Dhrona',        15, 'Active', NOW(), NOW()),
  ('Room C - Brahma',        20, 'Active', NOW(), NOW()),
  ('Room D - Maheshwara',    15, 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ── 4. SPRINTS ───────────────────────────────────────────────────

INSERT INTO sprints (title, technology, cohort, cohorts_json, trainer_id, created_by, room, start_date, end_date, sprint_start_time, sprint_end_time, status, instructions, created_at, updated_at)
VALUES
  -- Java Sprint C1/C2
  ('Java Sprint - C1/C2', 'Java', 'C1',
   '[{"technology":"Java","cohort":"C1"},{"technology":"Java","cohort":"C2"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Room A - Sandeepa',
   '2026-03-25', '2026-04-30',
   '12:00 PM', '01:00 PM',
   'Scheduled',
   'Java training for C1 and C2 cohorts. Session: 12:00-13:00.',
   NOW(), NOW()),

  -- Python Sprint C3
  ('Python Sprint - C3', 'Python', 'C3',
   '[{"technology":"Python","cohort":"C3"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Room B - Dhrona',
   '2026-03-25', '2026-04-30',
   '02:00 PM', '03:00 PM',
   'Scheduled',
   'Python training for C3 cohort. Session: 14:00-15:00.',
   NOW(), NOW()),

  -- Devops Sprint C4
  ('Devops Sprint - C4', 'Devops', 'C4',
   '[{"technology":"Devops","cohort":"C4"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Room C - Brahma',
   '2026-03-25', '2026-04-30',
   '03:00 PM', '04:00 PM',
   'Scheduled',
   'Devops training for C4 cohort. Session: 15:00-16:00.',
   NOW(), NOW())

ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ── 5. EMPLOYEES — Java C1 + C2 ─────────────────────────────────

INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at)
VALUES
  ('2431035', 'Peram Raghunadha Reddy',      '2431035@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531015', 'Asu Bhaskar Chaitanya',        '2531015@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531025', 'Gudi Anil Reddy',              '2531025@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531005', 'Kummari Pavan Kumar',          '2531005@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531018', 'Gummadi Rakesh',               '2531018@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2431031', 'Medeboina Srinivas',           '2431031@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2431041', 'Velugu Aparna',                '2431041@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2431042', 'Reddy Gaari Navaneeth Reddy',  '2431042@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531003', 'Kasarla Avinash Kumar',        '2531003@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531017', 'Pishke Saikiran',              '2531017@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531006', 'Konnoju Shireesha',            '2531006@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  ('2531010', 'Anil Choppari',                '2531010@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW()),
  -- Java C2
  ('2531019', 'Rohith Shunkishela',           '2531019@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531021', 'Bodapati Sai Krishna',         '2531021@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531022', 'Vaduru Dileep Sai',            '2531022@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531024', 'Kongari Nikhil',               '2531024@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531038', 'Nimmaturi Ashok Kumar',        '2531038@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531047', 'Nampally Radhika',             '2531047@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531050', 'Kammari Vamshi Krishna Chary', '2531050@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531014', 'Thontla Yedukondala Reddy',    '2531014@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531031', 'Namburi Suneetha',             '2531031@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531033', 'Annapureddy Sravani',          '2531033@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531036', 'Peddinti Ramya',               '2531036@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531051', 'Badi Vinitha',                 '2531051@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW()),
  ('2531039', 'Bathina Ashok',                '2531039@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), cohort = VALUES(cohort), email = VALUES(email), updated_at = NOW();

-- ── 6. SPRINT_EMPLOYEES ──────────────────────────────────────────

-- Java C1 + C2
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2')
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- ── 7. ATTENDANCE ────────────────────────────────────────────────

SET @trainer_id = (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in');

INSERT IGNORE INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1), e.id, '2026-03-25', 'Present', '12:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2');

INSERT IGNORE INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1), e.id, '2026-03-26', 'Present', '12:10 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2');

-- ── 8. VERIFY ────────────────────────────────────────────────────

SELECT 'Users'           AS tbl, COUNT(*) AS cnt FROM users           UNION ALL
SELECT 'Sprints',                COUNT(*)        FROM sprints          UNION ALL
SELECT 'Employees',              COUNT(*)        FROM employees        UNION ALL
SELECT 'Sprint_Employees',       COUNT(*)        FROM sprint_employees UNION ALL
SELECT 'Attendance',             COUNT(*)        FROM attendance;
