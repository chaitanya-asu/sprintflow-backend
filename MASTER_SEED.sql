-- ═══════════════════════════════════════════════════════════════════════════════
-- SprintFlow — MASTER SEED FILE (Complete & Clean)
-- All data consolidated: Users, Rooms, Employees (5 technologies), Sprints, Attendance
-- No glitches, standardized cohort naming (C1, C2, C3, etc.)
-- ═══════════════════════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 1: USERS (5 total: 2 Managers, 2 HR, 1 Trainer)
-- Password: Admin@123 (bcrypt hash)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE users;

INSERT INTO users (name, email, password, role, phone, department, trainer_role, status, joined_date, password_changed, created_at, updated_at)
VALUES
  ('Surya Prakash',        'surya@sprintflow.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000001', 'Management',    NULL,                 'Active', '2020-01-01', 1, NOW(), NOW()),
  ('Aswini Pasam',         'a.pasam@ajacs.in',            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000002', 'Management',    NULL,                 'Active', '2020-01-01', 1, NOW(), NOW()),
  ('Satwika',              's.lakkampally@ajacs.in',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000003', 'HR',            NULL,                 'Active', '2021-06-01', 1, NOW(), NOW()),
  ('Nikitha',              'nikitha@ajacs.in',             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000004', 'HR',            NULL,                 'Active', '2021-06-01', 1, NOW(), NOW()),
  ('Surya Posanapally',    's.posanapally@ajacs.in',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000005', 'Communication', 'Manager-Trainings',  'Active', '2020-07-10', 1, NOW(), NOW());

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 2: ROOMS (9 total)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE rooms;

INSERT INTO rooms (name, capacity, status, created_at, updated_at) VALUES
  ('Room A - Sandeepa',      30, 'Active', NOW(), NOW()),
  ('Room B - Dhrona',        25, 'Active', NOW(), NOW()),
  ('Room C - Brahma',        20, 'Active', NOW(), NOW()),
  ('Room D - Maheshwara',    35, 'Active', NOW(), NOW()),
  ('Training Room 1',        30, 'Active', NOW(), NOW()),
  ('Training Room 2',        25, 'Active', NOW(), NOW()),
  ('Training Room 3',        20, 'Active', NOW(), NOW()),
  ('Training Room 4',        30, 'Active', NOW(), NOW()),
  ('Training Room 5',        25, 'Active', NOW(), NOW());

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 3: EMPLOYEES (73 total across 5 technologies)
-- Cohort naming: C1, C2, C3, C4, C5 (standardized)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE employees;

-- JAVA COHORT 1 (12 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
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
  ('2531010', 'Anil Choppari',                '2531010@ajacs.in', 'Java', 'C1', 'Technology', 'Active', NOW(), NOW());

-- JAVA COHORT 2 (13 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
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
  ('2531039', 'Bathina Ashok',                '2531039@ajacs.in', 'Java', 'C2', 'Technology', 'Active', NOW(), NOW());

-- PYTHON COHORT 1 (10 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('PC1001', 'Arjun Mehta',        'pc1001@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1002', 'Sneha Kulkarni',     'pc1002@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1003', 'Rahul Sharma',       'pc1003@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1004', 'Priya Reddy',        'pc1004@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1005', 'Kiran Babu',         'pc1005@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1006', 'Divya Nair',         'pc1006@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1007', 'Suresh Rao',         'pc1007@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1008', 'Ananya Iyer',        'pc1008@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1009', 'Vikram Das',         'pc1009@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW()),
  ('PC1010', 'Pooja Verma',        'pc1010@ajacs.in', 'Python', 'C3', 'Technology', 'Active', NOW(), NOW());

-- DEVOPS COHORT 1 (8 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('DC1001', 'Rohit Kumar',        'dc1001@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1002', 'Meera Joshi',        'dc1002@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1003', 'Anil Chandra',       'dc1003@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1004', 'Lakshmi Prasad',     'dc1004@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1005', 'Naveen Reddy',       'dc1005@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1006', 'Sravani Patel',      'dc1006@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1007', 'Manoj Singh',        'dc1007@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW()),
  ('DC1008', 'Kavitha Rao',        'dc1008@ajacs.in', 'Devops', 'C4', 'Technology', 'Active', NOW(), NOW());

-- DOTNET COHORT 1 (8 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('NC1001', 'Rajesh Kumar',        'nc1001@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1002', 'Priya Sharma',        'nc1002@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1003', 'Amit Singh',          'nc1003@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1004', 'Neha Gupta',          'nc1004@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1005', 'Vikash Yadav',        'nc1005@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1006', 'Kavya Reddy',         'nc1006@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1007', 'Ravi Teja',           'nc1007@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW()),
  ('NC1008', 'Swathi Nair',         'nc1008@ajacs.in', 'DotNet', 'C5', 'Technology', 'Active', NOW(), NOW());

-- SALESFORCE COHORT 1 (6 employees)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('SC1001', 'Manoj Patel',         'sc1001@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW()),
  ('SC1002', 'Deepika Jain',        'sc1002@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW()),
  ('SC1003', 'Suresh Babu',         'sc1003@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW()),
  ('SC1004', 'Lakshmi Devi',        'sc1004@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW()),
  ('SC1005', 'Harish Chandra',      'sc1005@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW()),
  ('SC1006', 'Sita Ramesh',         'sc1006@ajacs.in', 'SalesForce', 'C6', 'Technology', 'Active', NOW(), NOW());

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 4: SPRINTS (5 total - one per technology)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE sprints;

INSERT INTO sprints (title, technology, cohort, cohorts_json, trainer_id, created_by, room, start_date, end_date, sprint_start_time, sprint_end_time, status, instructions, created_at, updated_at)
VALUES
  ('Java Sprint - C1/C2', 'Java', 'C1',
   '[{"technology":"Java","cohort":"C1"},{"technology":"Java","cohort":"C2"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 1',
   '2026-03-25', '2026-04-30',
   '12:00 PM', '01:00 PM',
   'Scheduled',
   'Java training for C1 and C2 cohorts',
   NOW(), NOW()),

  ('Python Sprint - C3', 'Python', 'C3',
   '[{"technology":"Python","cohort":"C3"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 2',
   '2026-03-25', '2026-04-30',
   '02:00 PM', '03:00 PM',
   'Scheduled',
   'Python training for C3 cohort',
   NOW(), NOW()),

  ('Devops Sprint - C4', 'Devops', 'C4',
   '[{"technology":"Devops","cohort":"C4"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 3',
   '2026-03-25', '2026-04-30',
   '03:00 PM', '04:00 PM',
   'Scheduled',
   'Devops training for C4 cohort',
   NOW(), NOW()),

  ('DotNet Sprint - C5', 'DotNet', 'C5',
   '[{"technology":"DotNet","cohort":"C5"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 4',
   '2026-04-01', '2026-05-15',
   '04:00 PM', '05:00 PM',
   'Scheduled',
   'DotNet training for C5 cohort',
   NOW(), NOW()),

  ('Salesforce Sprint - C6', 'SalesForce', 'C6',
   '[{"technology":"SalesForce","cohort":"C6"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 5',
   '2026-04-01', '2026-05-15',
   '05:00 PM', '06:00 PM',
   'Scheduled',
   'Salesforce training for C6 cohort',
   NOW(), NOW());

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 5: SPRINT_EMPLOYEES (Auto-enroll all employees)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE sprint_employees;

-- Java Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1), e.id, 'ENROLLED', NOW()
FROM employees e WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2');

-- Python Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT (SELECT id FROM sprints WHERE title = 'Python Sprint - C3' LIMIT 1), e.id, 'ENROLLED', NOW()
FROM employees e WHERE e.technology = 'Python' AND e.cohort = 'C3';

-- Devops Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT (SELECT id FROM sprints WHERE title = 'Devops Sprint - C4' LIMIT 1), e.id, 'ENROLLED', NOW()
FROM employees e WHERE e.technology = 'Devops' AND e.cohort = 'C4';

-- DotNet Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT (SELECT id FROM sprints WHERE title = 'DotNet Sprint - C5' LIMIT 1), e.id, 'ENROLLED', NOW()
FROM employees e WHERE e.technology = 'DotNet' AND e.cohort = 'C5';

-- Salesforce Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT (SELECT id FROM sprints WHERE title = 'Salesforce Sprint - C6' LIMIT 1), e.id, 'ENROLLED', NOW()
FROM employees e WHERE e.technology = 'SalesForce' AND e.cohort = 'C6';

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 6: ATTENDANCE (Sample data for all sprints)
-- ══════════════════════════════════════════════════════════════════════════════

TRUNCATE TABLE attendance;

SET @trainer_id = (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in');

-- Java attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1), e.id, '2026-03-25', 'Present', '12:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2');

-- Python attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Python Sprint - C3' LIMIT 1), e.id, '2026-03-25', 'Present', '02:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Python' AND e.cohort = 'C3';

-- Devops attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Devops Sprint - C4' LIMIT 1), e.id, '2026-03-25', 'Present', '03:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Devops' AND e.cohort = 'C4';

-- DotNet attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'DotNet Sprint - C5' LIMIT 1), e.id, '2026-04-01', 'Present', '04:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'DotNet' AND e.cohort = 'C5';

-- Salesforce attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT (SELECT id FROM sprints WHERE title = 'Salesforce Sprint - C6' LIMIT 1), e.id, '2026-04-01', 'Present', '05:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'SalesForce' AND e.cohort = 'C6';

-- ══════════════════════════════════════════════════════════════════════════════
-- SECTION 7: VERIFICATION & SUMMARY
-- ══════════════════════════════════════════════════════════════════════════════

SELECT '✓ DATA LOAD COMPLETE' AS status;
SELECT '=== SUMMARY ===' AS info;
SELECT 'Users' AS entity, COUNT(*) AS count FROM users UNION ALL
SELECT 'Rooms', COUNT(*) FROM rooms UNION ALL
SELECT 'Employees', COUNT(*) FROM employees UNION ALL
SELECT 'Sprints', COUNT(*) FROM sprints UNION ALL
SELECT 'Sprint_Employees', COUNT(*) FROM sprint_employees UNION ALL
SELECT 'Attendance', COUNT(*) FROM attendance;

SELECT '=== EMPLOYEES BY TECHNOLOGY ===' AS info;
SELECT technology, cohort, COUNT(*) as count FROM employees GROUP BY technology, cohort ORDER BY technology, cohort;

SELECT '=== SPRINTS ===' AS info;
SELECT id, title, technology, cohort, status FROM sprints ORDER BY id;

SELECT '=== SPRINT ENROLLMENTS ===' AS info;
SELECT s.title, COUNT(se.id) as enrolled_count
FROM sprints s
LEFT JOIN sprint_employees se ON s.id = se.sprint_id
GROUP BY s.id, s.title
ORDER BY s.id;
