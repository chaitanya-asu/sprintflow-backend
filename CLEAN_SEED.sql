-- ═══════════════════════════════════════════════════════════════════════════════
-- SprintFlow — CLEAN CONSOLIDATED SEED FILE
-- Standardized Cohort Naming: JavaC1, JavaC2, PythonC1, DevopsC1, DotNetC1, SalesForceC1
-- ═══════════════════════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── STEP 1: CLEAR EXISTING DATA (OPTIONAL - UNCOMMENT TO USE) ──────────────────
-- DELETE FROM attendance;
-- DELETE FROM sprint_employees;
-- DELETE FROM sprints;
-- DELETE FROM employees;
-- DELETE FROM users;
-- DELETE FROM rooms;

-- ── STEP 2: INSERT USERS ──────────────────────────────────────────────────────
-- Password: Admin@123 (hashed with bcrypt)
INSERT INTO users (name, email, password, role, phone, department, trainer_role, status, joined_date, password_changed, created_at, updated_at)
VALUES
  ('Surya Prakash',        'surya@sprintflow.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000001', 'Management',    NULL,                 'Active', '2020-01-01', 1, NOW(), NOW()),
  ('Aswini Pasam',         'a.pasam@ajacs.in',            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'MANAGER', '9000000002', 'Management',    NULL,                 'Active', '2020-01-01', 1, NOW(), NOW()),
  ('Satwika',              's.lakkampally@ajacs.in',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000003', 'HR',            NULL,                 'Active', '2021-06-01', 1, NOW(), NOW()),
  ('Nikitha',              'nikitha@ajacs.in',             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'HR',      '9000000004', 'HR',            NULL,                 'Active', '2021-06-01', 1, NOW(), NOW()),
  ('Surya Posanapally',    's.posanapally@ajacs.in',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyc5AlL2i', 'TRAINER', '9000000005', 'Communication', 'Manager-Trainings',  'Active', '2020-07-10', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  role = VALUES(role),
  department = VALUES(department),
  trainer_role = VALUES(trainer_role),
  updated_at = NOW();

-- ── STEP 3: INSERT ROOMS ──────────────────────────────────────────────────────
INSERT INTO rooms (name, capacity, status, created_at, updated_at) VALUES
  ('Room A - Sandeepa',      30, 'Active', NOW(), NOW()),
  ('Room B - Dhrona',        25, 'Active', NOW(), NOW()),
  ('Room C - Brahma',        20, 'Active', NOW(), NOW()),
  ('Room D - Maheshwara',    35, 'Active', NOW(), NOW()),
  ('Training Room 1',        30, 'Active', NOW(), NOW()),
  ('Training Room 2',        25, 'Active', NOW(), NOW()),
  ('Training Room 3',        20, 'Active', NOW(), NOW()),
  ('Training Room 4',        30, 'Active', NOW(), NOW()),
  ('Training Room 5',        25, 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  capacity = VALUES(capacity),
  status = VALUES(status),
  updated_at = NOW();

-- ── STEP 4: INSERT EMPLOYEES ─────────────────────────────────────────────────
-- JAVA COHORT 1 (JavaC1)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('2431035', 'Peram Raghunadha Reddy',      '2431035@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531015', 'Asu Bhaskar Chaitanya',        '2531015@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531025', 'Gudi Anil Reddy',              '2531025@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531005', 'Kummari Pavan Kumar',          '2531005@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531018', 'Gummadi Rakesh',               '2531018@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2431031', 'Medeboina Srinivas',           '2431031@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2431041', 'Velugu Aparna',                '2431041@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2431042', 'Reddy Gaari Navaneeth Reddy',  '2431042@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531003', 'Kasarla Avinash Kumar',        '2531003@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531017', 'Pishke Saikiran',              '2531017@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531006', 'Konnoju Shireesha',            '2531006@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW()),
  ('2531010', 'Anil Choppari',                '2531010@ajacs.in', 'Java', 'JavaC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- JAVA COHORT 2 (JavaC2)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('2531019', 'Rohith Shunkishela',           '2531019@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531021', 'Bodapati Sai Krishna',         '2531021@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531022', 'Vaduru Dileep Sai',            '2531022@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531024', 'Kongari Nikhil',               '2531024@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531038', 'Nimmaturi Ashok Kumar',        '2531038@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531047', 'Nampally Radhika',             '2531047@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531050', 'Kammari Vamshi Krishna Chary', '2531050@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531014', 'Thontla Yedukondala Reddy',    '2531014@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531031', 'Namburi Suneetha',             '2531031@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531033', 'Annapureddy Sravani',          '2531033@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531036', 'Peddinti Ramya',               '2531036@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531051', 'Badi Vinitha',                 '2531051@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW()),
  ('2531039', 'Bathina Ashok',                '2531039@ajacs.in', 'Java', 'JavaC2', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- PYTHON COHORT 1 (PythonC1)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('PC1001', 'Arjun Mehta',        'pc1001@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1002', 'Sneha Kulkarni',     'pc1002@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1003', 'Rahul Sharma',       'pc1003@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1004', 'Priya Reddy',        'pc1004@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1005', 'Kiran Babu',         'pc1005@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1006', 'Divya Nair',         'pc1006@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1007', 'Suresh Rao',         'pc1007@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1008', 'Ananya Iyer',        'pc1008@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1009', 'Vikram Das',         'pc1009@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW()),
  ('PC1010', 'Pooja Verma',        'pc1010@ajacs.in', 'Python', 'PythonC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- DEVOPS COHORT 1 (DevopsC1)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('DC1001', 'Rohit Kumar',        'dc1001@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1002', 'Meera Joshi',        'dc1002@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1003', 'Anil Chandra',       'dc1003@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1004', 'Lakshmi Prasad',     'dc1004@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1005', 'Naveen Reddy',       'dc1005@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1006', 'Sravani Patel',      'dc1006@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1007', 'Manoj Singh',        'dc1007@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW()),
  ('DC1008', 'Kavitha Rao',        'dc1008@ajacs.in', 'Devops', 'DevopsC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- DOTNET COHORT 1 (DotNetC1)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('NC1001', 'Rajesh Kumar',        'nc1001@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1002', 'Priya Sharma',        'nc1002@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1003', 'Amit Singh',          'nc1003@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1004', 'Neha Gupta',          'nc1004@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1005', 'Vikash Yadav',        'nc1005@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1006', 'Kavya Reddy',         'nc1006@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1007', 'Ravi Teja',           'nc1007@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW()),
  ('NC1008', 'Swathi Nair',         'nc1008@ajacs.in', 'DotNet', 'DotNetC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- SALESFORCE COHORT 1 (SalesForceC1)
INSERT INTO employees (emp_id, name, email, technology, cohort, department, status, created_at, updated_at) VALUES
  ('SC1001', 'Manoj Patel',         'sc1001@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1002', 'Deepika Jain',        'sc1002@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1003', 'Suresh Babu',         'sc1003@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1004', 'Lakshmi Devi',        'sc1004@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1005', 'Harish Chandra',      'sc1005@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW()),
  ('SC1006', 'Sita Ramesh',         'sc1006@ajacs.in', 'SalesForce', 'SalesForceC1', 'Technology', 'Active', NOW(), NOW())
ON DUPLICATE KEY UPDATE cohort = VALUES(cohort), technology = VALUES(technology), updated_at = NOW();

-- ── STEP 5: INSERT SPRINTS ───────────────────────────────────────────────────
INSERT INTO sprints (title, technology, cohort, cohorts_json, trainer_id, created_by, room, start_date, end_date, sprint_start_time, sprint_end_time, status, instructions, created_at, updated_at)
VALUES
  ('Java Sprint - JavaC1/JavaC2', 'Java', 'JavaC1',
   '[{"technology":"Java","cohort":"JavaC1"},{"technology":"Java","cohort":"JavaC2"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 1',
   '2026-03-25', '2026-04-30',
   '12:00 PM', '01:00 PM',
   'Scheduled',
   'Java training for JavaC1 and JavaC2 cohorts. Session: 12:00-13:00.',
   NOW(), NOW()),

  ('Python Sprint - PythonC1', 'Python', 'PythonC1',
   '[{"technology":"Python","cohort":"PythonC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 2',
   '2026-03-25', '2026-04-30',
   '02:00 PM', '03:00 PM',
   'Scheduled',
   'Python training for PythonC1 cohort. Session: 14:00-15:00.',
   NOW(), NOW()),

  ('Devops Sprint - DevopsC1', 'Devops', 'DevopsC1',
   '[{"technology":"Devops","cohort":"DevopsC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 3',
   '2026-03-25', '2026-04-30',
   '03:00 PM', '04:00 PM',
   'Scheduled',
   'Devops training for DevopsC1 cohort. Session: 15:00-16:00.',
   NOW(), NOW()),

  ('DotNet Sprint - DotNetC1', 'DotNet', 'DotNetC1',
   '[{"technology":"DotNet","cohort":"DotNetC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 4',
   '2026-04-01', '2026-05-15',
   '04:00 PM', '05:00 PM',
   'Scheduled',
   '.NET training for DotNetC1 cohort. Session: 16:00-17:00.',
   NOW(), NOW()),

  ('Salesforce Sprint - SalesForceC1', 'SalesForce', 'SalesForceC1',
   '[{"technology":"SalesForce","cohort":"SalesForceC1"}]',
   (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in'),
   (SELECT id FROM users WHERE email = 's.lakkampally@ajacs.in'),
   'Training Room 5',
   '2026-04-01', '2026-05-15',
   '05:00 PM', '06:00 PM',
   'Scheduled',
   'Salesforce training for SalesForceC1 cohort. Session: 17:00-18:00.',
   NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ── STEP 6: AUTO-ENROLL EMPLOYEES IN SPRINTS ──────────────────────────────────
-- Java Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Java Sprint - JavaC1/JavaC2' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Java' AND e.cohort IN ('JavaC1', 'JavaC2')
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- Python Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Python Sprint - PythonC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Python' AND e.cohort = 'PythonC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- Devops Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Devops Sprint - DevopsC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'Devops' AND e.cohort = 'DevopsC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- DotNet Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'DotNet Sprint - DotNetC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'DotNet' AND e.cohort = 'DotNetC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- Salesforce Sprint
INSERT INTO sprint_employees (sprint_id, employee_id, status, enrolled_at)
SELECT
  (SELECT id FROM sprints WHERE title = 'Salesforce Sprint - SalesForceC1' LIMIT 1),
  e.id, 'ENROLLED', NOW()
FROM employees e
WHERE e.technology = 'SalesForce' AND e.cohort = 'SalesForceC1'
ON DUPLICATE KEY UPDATE status = 'ENROLLED';

-- ── STEP 7: INSERT SAMPLE ATTENDANCE DATA ─────────────────────────────────────
SET @java_sprint = (SELECT id FROM sprints WHERE title = 'Java Sprint - JavaC1/JavaC2' LIMIT 1);
SET @python_sprint = (SELECT id FROM sprints WHERE title = 'Python Sprint - PythonC1' LIMIT 1);
SET @devops_sprint = (SELECT id FROM sprints WHERE title = 'Devops Sprint - DevopsC1' LIMIT 1);
SET @trainer_id = (SELECT id FROM users WHERE email = 's.posanapally@ajacs.in');

-- Java attendance (sample)
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @java_sprint, e.id, '2026-03-25', 'Present', '12:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Java' AND e.cohort IN ('JavaC1', 'JavaC2')
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- Python attendance (sample)
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @python_sprint, e.id, '2026-03-25', 'Present', '02:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Python' AND e.cohort = 'PythonC1'
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- Devops attendance (sample)
INSERT INTO attendance (sprint_id, employee_id, attendance_date, status, check_in_time, submitted, marked_by, created_at, updated_at)
SELECT @devops_sprint, e.id, '2026-03-25', 'Present', '03:05 PM', 1, @trainer_id, NOW(), NOW()
FROM employees e WHERE e.technology = 'Devops' AND e.cohort = 'DevopsC1'
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

-- ── STEP 8: VERIFICATION QUERIES ──────────────────────────────────────────────
SELECT '=== DATA SUMMARY ===' AS info;
SELECT 'Users' AS table_name, COUNT(*) AS count FROM users UNION ALL
SELECT 'Rooms', COUNT(*) FROM rooms UNION ALL
SELECT 'Employees', COUNT(*) FROM employees UNION ALL
SELECT 'Sprints', COUNT(*) FROM sprints UNION ALL
SELECT 'Sprint_Employees', COUNT(*) FROM sprint_employees UNION ALL
SELECT 'Attendance', COUNT(*) FROM attendance;

SELECT '=== EMPLOYEES BY COHORT ===' AS info;
SELECT technology, cohort, COUNT(*) as count FROM employees GROUP BY technology, cohort ORDER BY technology, cohort;

SELECT '=== SPRINTS ===' AS info;
SELECT id, title, technology, cohort, status FROM sprints ORDER BY id;

SELECT '=== SPRINT ENROLLMENTS ===' AS info;
SELECT s.title, COUNT(se.id) as enrolled_count
FROM sprints s
LEFT JOIN sprint_employees se ON s.id = se.sprint_id
GROUP BY s.id, s.title
ORDER BY s.id;
