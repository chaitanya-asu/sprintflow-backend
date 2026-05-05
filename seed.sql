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
  -- Trainer
  ('Surya Posanapally', 's.posanapally@ajacs.in',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000005', 'Communication', 'Manager-Trainings',  'Active', '2020-07-10', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name       = VALUES(name),
  role       = VALUES(role),
  department = VALUES(department),
  trainer_role = VALUES(trainer_role),
  updated_at = NOW();

-- ── 2. SPRINTS ───────────────────────────────────────────────────

INSERT INTO sprints (title, technology, cohort, cohorts_json, trainer_id, created_by, room, start_date, end_date, sprint_start_time, sprint_end_time, status, instructions, created_at, updated_at)
VALUES
  -- Java Sprint C1 + C2
  ('Java Sprint - C1/C2', 'Java', 'C1',
   '[{"technology":"Java","cohort":"C1"},{"technology":"Java","cohort":"C2"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 1',
   '2026-03-25', '2026-04-30',
   '12:00 PM', '01:00 PM',
   'Scheduled',
   'Java training for C1 and C2 cohorts. Session: 12:00-13:00.',
   NOW(), NOW()),

  -- Python Sprint C4
  ('Python Sprint - C4', 'Python', 'C4',
   '[{"technology":"Python","cohort":"C4"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 2',
   '2026-03-25', '2026-04-30',
   '02:00 PM', '03:00 PM',
   'Scheduled',
   'Python training for C4 cohort. Session: 14:00-15:00.',
   NOW(), NOW()),

  -- Devops Sprint C7
  ('Devops Sprint - C7', 'Devops', 'C7',
   '[{"technology":"Devops","cohort":"C7"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 3',
   '2026-03-25', '2026-04-30',
   '03:00 PM', '04:00 PM',
   'Scheduled',
   'Devops training for C7 cohort. Session: 15:00-16:00.',
   NOW(), NOW())

ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ── 3. EMPLOYEES — Java C1 ──────────────────────────────────────

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

-- ── 4. SPRINT_EMPLOYEES ──────────────────────────────────────────

-- Java C1 + C2
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Java Sprint - C1/C2' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Java' AND e.cohort IN ('C1', 'C2')
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- ── 5. VERIFY ────────────────────────────────────────────────────

SELECT 'Users'           AS tbl, COUNT(*) AS cnt FROM users           UNION ALL
SELECT 'Sprints',                COUNT(*)        FROM sprints          UNION ALL
SELECT 'Employees',              COUNT(*)        FROM employees        UNION ALL
SELECT 'Sprint_Employees',       COUNT(*)        FROM sprint_employees UNION ALL
SELECT 'Attendance',             COUNT(*)        FROM attendance;
-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Python PC1 + Devops DC1 Seed
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── Python PC1 Employees (10) ─────────────────────────────────
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at)
VALUES
  ('PC1001', 'Arjun Mehta',        'pc1001@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1002', 'Sneha Kulkarni',     'pc1002@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1003', 'Rahul Sharma',       'pc1003@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1004', 'Priya Reddy',        'pc1004@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1005', 'Kiran Babu',         'pc1005@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1006', 'Divya Nair',         'pc1006@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1007', 'Suresh Rao',         'pc1007@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1008', 'Ananya Iyer',        'pc1008@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1009', 'Vikram Das',         'pc1009@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1010', 'Pooja Verma',        'pc1010@ajacs.in', 'Python', 'PC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

-- ── Devops DC1 Employees (8) ──────────────────────────────────
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at)
VALUES
  ('DC1001', 'Rohit Kumar',        'dc1001@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1002', 'Meera Joshi',        'dc1002@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1003', 'Anil Chandra',       'dc1003@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1004', 'Lakshmi Prasad',     'dc1004@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1005', 'Naveen Reddy',       'dc1005@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1006', 'Sravani Patel',      'dc1006@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1007', 'Manoj Singh',        'dc1007@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1008', 'Kavitha Rao',        'dc1008@ajacs.in', 'Devops', 'DC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

-- ── Enroll Python PC1 into Python Sprint ─────────────────────
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Python Sprint - PC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Python' AND e.cohort = 'PC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- ── Enroll Devops DC1 into Devops Sprint ─────────────────────
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Devops Sprint - DC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Devops' AND e.cohort = 'DC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- ── Attendance for Python PC1 (varied data) ───────────────────
SET @python_sprint = (SELECT id FROM sprints WHERE title = 'Python Sprint - PC1' LIMIT 1);
SET @trainer_id    = (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in');

INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @python_sprint, e.id, d.dt, d.st, IF(d.st='Present','02:05 PM', NULL), true, @trainer_id, NOW(), NOW()
FROM employees e
JOIN (
  SELECT 'PC1001' AS emp_id, '2026-03-25' AS dt, 'Present' AS st UNION ALL
  SELECT 'PC1001', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1001', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1001', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1001', '2026-03-31', 'Absent'  UNION ALL
  SELECT 'PC1002', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1002', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1002', '2026-03-27', 'Late'    UNION ALL
  SELECT 'PC1002', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1002', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1003', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1003', '2026-03-26', 'Absent'  UNION ALL
  SELECT 'PC1003', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1003', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1003', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1004', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1004', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1004', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1004', '2026-03-30', 'Late'    UNION ALL
  SELECT 'PC1004', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1005', '2026-03-25', 'Absent'  UNION ALL
  SELECT 'PC1005', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1005', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1005', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1005', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1006', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1006', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1006', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1006', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1006', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1007', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1007', '2026-03-26', 'Late'    UNION ALL
  SELECT 'PC1007', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1007', '2026-03-30', 'Absent'  UNION ALL
  SELECT 'PC1007', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1008', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1008', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1008', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1008', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1008', '2026-03-31', 'Present' UNION ALL
  SELECT 'PC1009', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1009', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1009', '2026-03-27', 'Absent'  UNION ALL
  SELECT 'PC1009', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1009', '2026-03-31', 'Late'    UNION ALL
  SELECT 'PC1010', '2026-03-25', 'Present' UNION ALL
  SELECT 'PC1010', '2026-03-26', 'Present' UNION ALL
  SELECT 'PC1010', '2026-03-27', 'Present' UNION ALL
  SELECT 'PC1010', '2026-03-30', 'Present' UNION ALL
  SELECT 'PC1010', '2026-03-31', 'Present'
) d ON e.emp_id = d.emp_id
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- ── Attendance for Devops DC1 ─────────────────────────────────
SET @devops_sprint = (SELECT id FROM sprints WHERE title = 'Devops Sprint - DC1' LIMIT 1);

INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @devops_sprint, e.id, d.dt, d.st, IF(d.st='Present','03:05 PM', NULL), true, @trainer_id, NOW(), NOW()
FROM employees e
JOIN (
  SELECT 'DC1001' AS emp_id, '2026-03-25' AS dt, 'Present' AS st UNION ALL
  SELECT 'DC1001', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1001', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1001', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1001', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1002', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1002', '2026-03-26', 'Late'    UNION ALL
  SELECT 'DC1002', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1002', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1002', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1003', '2026-03-25', 'Absent'  UNION ALL
  SELECT 'DC1003', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1003', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1003', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1003', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1004', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1004', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1004', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1004', '2026-03-30', 'Absent'  UNION ALL
  SELECT 'DC1004', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1005', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1005', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1005', '2026-03-27', 'Late'    UNION ALL
  SELECT 'DC1005', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1005', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1006', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1006', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1006', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1006', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1006', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1007', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1007', '2026-03-26', 'Absent'  UNION ALL
  SELECT 'DC1007', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1007', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1007', '2026-03-31', 'Present' UNION ALL
  SELECT 'DC1008', '2026-03-25', 'Present' UNION ALL
  SELECT 'DC1008', '2026-03-26', 'Present' UNION ALL
  SELECT 'DC1008', '2026-03-27', 'Present' UNION ALL
  SELECT 'DC1008', '2026-03-30', 'Present' UNION ALL
  SELECT 'DC1008', '2026-03-31', 'Late'
) d ON e.emp_id = d.emp_id
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- ── Verify ────────────────────────────────────────────────────
SELECT technology, cohort, COUNT(*) as employees FROM employees GROUP BY technology, cohort ORDER BY technology, cohort;
SELECT 'Attendance' AS tbl, COUNT(*) FROM attendance;
-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Additional Sprints (5 Total)
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- Add 2 more sprints to make it 5 total sprints

INSERT INTO sprints (title, technology, cohort, cohorts_json, trainer_id, created_by, room, start_date, end_date, sprint_start_time, sprint_end_time, status, instructions, created_at, updated_at)
VALUES
  -- .NET Sprint (new technology)
  ('DotNet Sprint - NC1', 'DotNet', 'NC1',
   '[{"technology":"DotNet","cohort":"NC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 4',
   '2026-04-01', '2026-05-15',
   '04:00 PM', '05:00 PM',
   'Scheduled',
   '.NET training for NC1 cohort. Session: 16:00-17:00.',
   NOW(), NOW()),

  -- Salesforce Sprint (new technology)
  ('Salesforce Sprint - SC1', 'SalesForce', 'SC1',
   '[{"technology":"SalesForce","cohort":"SC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 5',
   '2026-04-01', '2026-05-15',
   '05:00 PM', '06:00 PM',
   'Scheduled',
   'Salesforce training for SC1 cohort. Session: 17:00-18:00.',
   NOW(), NOW())

ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Add .NET students
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at)
VALUES
  ('NC1001', 'Rajesh Kumar',        'nc1001@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1002', 'Priya Sharma',        'nc1002@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1003', 'Amit Singh',          'nc1003@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1004', 'Neha Gupta',          'nc1004@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1005', 'Vikash Yadav',        'nc1005@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1006', 'Kavya Reddy',         'nc1006@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1007', 'Ravi Teja',           'nc1007@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1008', 'Swathi Nair',         'nc1008@ajacs.in', 'DotNet', 'NC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

-- Add Salesforce students
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at)
VALUES
  ('SC1001', 'Manoj Patel',         'sc1001@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1002', 'Deepika Jain',        'sc1002@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1003', 'Suresh Babu',         'sc1003@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1004', 'Lakshmi Devi',        'sc1004@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1005', 'Harish Chandra',      'sc1005@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1006', 'Sita Ramesh',         'sc1006@ajacs.in', 'SalesForce', 'SC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

-- Enroll .NET students
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'DotNet Sprint - NC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'DotNet' AND e.cohort = 'NC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- Enroll Salesforce students
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Salesforce Sprint - SC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'SalesForce' AND e.cohort = 'SC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- Add some attendance data for the new sprints
SET @dotnet_sprint = (SELECT id FROM sprints WHERE title = 'DotNet Sprint - NC1' LIMIT 1);
SET @salesforce_sprint = (SELECT id FROM sprints WHERE title = 'Salesforce Sprint - SC1' LIMIT 1);
SET @trainer_id = (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in');

-- .NET attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @dotnet_sprint, e.id, '2026-04-01', 'Present', '04:05 PM', true, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'DotNet' AND e.cohort = 'NC1'
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- Salesforce attendance
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @salesforce_sprint, e.id, '2026-04-01', 'Present', '05:05 PM', true, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'SalesForce' AND e.cohort = 'SC1'
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- Verify final counts
SELECT 'Final Sprint Count' AS info, COUNT(*) AS total FROM sprints;
SELECT 'Final Employee Count' AS info, COUNT(*) AS total FROM employees;
SELECT 'Technologies' AS info, technology, COUNT(*) AS students FROM employees GROUP BY technology;
