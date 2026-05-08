-- ── 12. SEED EMPLOYEES FOR ALL COHORTS ──────────────────────────
-- Populate employees across all technologies and cohorts
-- Technologies: Java, Python, Devops, DotNet, SalesForce
-- Cohorts: C1, C2, C3, C4

-- Java Cohort 2 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP001', 'Rajesh Kumar', 'rajesh.kumar@company.com', '9876543210', 'Java', 'C2', 'Engineering', 'Active', NOW(), NOW()),
('EMP002', 'Priya Singh', 'priya.singh@company.com', '9876543211', 'Java', 'C2', 'Engineering', 'Active', NOW(), NOW()),
('EMP003', 'Amit Patel', 'amit.patel@company.com', '9876543212', 'Java', 'C2', 'Engineering', 'Active', NOW(), NOW()),
('EMP004', 'Neha Sharma', 'neha.sharma@company.com', '9876543213', 'Java', 'C2', 'Engineering', 'Active', NOW(), NOW()),
('EMP005', 'Vikram Singh', 'vikram.singh@company.com', '9876543214', 'Java', 'C2', 'Engineering', 'Active', NOW(), NOW());

-- Java Cohort 3 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP006', 'Arjun Verma', 'arjun.verma@company.com', '9876543215', 'Java', 'C3', 'Engineering', 'Active', NOW(), NOW()),
('EMP007', 'Divya Nair', 'divya.nair@company.com', '9876543216', 'Java', 'C3', 'Engineering', 'Active', NOW(), NOW()),
('EMP008', 'Sanjay Gupta', 'sanjay.gupta@company.com', '9876543217', 'Java', 'C3', 'Engineering', 'Active', NOW(), NOW()),
('EMP009', 'Anjali Reddy', 'anjali.reddy@company.com', '9876543218', 'Java', 'C3', 'Engineering', 'Active', NOW(), NOW()),
('EMP010', 'Rohan Desai', 'rohan.desai@company.com', '9876543219', 'Java', 'C3', 'Engineering', 'Active', NOW(), NOW());

-- Java Cohort 4 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP011', 'Karan Singh', 'karan.singh@company.com', '9876543220', 'Java', 'C4', 'Engineering', 'Active', NOW(), NOW()),
('EMP012', 'Pooja Sharma', 'pooja.sharma@company.com', '9876543221', 'Java', 'C4', 'Engineering', 'Active', NOW(), NOW()),
('EMP013', 'Nikhil Joshi', 'nikhil.joshi@company.com', '9876543222', 'Java', 'C4', 'Engineering', 'Active', NOW(), NOW()),
('EMP014', 'Shreya Iyer', 'shreya.iyer@company.com', '9876543223', 'Java', 'C4', 'Engineering', 'Active', NOW(), NOW()),
('EMP015', 'Aditya Rao', 'aditya.rao@company.com', '9876543224', 'Java', 'C4', 'Engineering', 'Active', NOW(), NOW());

-- Python Cohort 1 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP016', 'Meena Iyer', 'meena.iyer@company.com', '9876543225', 'Python', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP017', 'Surya Prakash', 'surya.prakash@company.com', '9876543226', 'Python', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP018', 'Ravi Kumar', 'ravi.kumar@company.com', '9876543227', 'Python', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP019', 'Sneha Gupta', 'sneha.gupta@company.com', '9876543228', 'Python', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP020', 'Deepak Nair', 'deepak.nair@company.com', '9876543229', 'Python', 'C1', 'Engineering', 'Active', NOW(), NOW());

-- Devops Cohort 1 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP021', 'Harish Reddy', 'harish.reddy@company.com', '9876543230', 'Devops', 'C1', 'Infrastructure', 'Active', NOW(), NOW()),
('EMP022', 'Kavya Singh', 'kavya.singh@company.com', '9876543231', 'Devops', 'C1', 'Infrastructure', 'Active', NOW(), NOW()),
('EMP023', 'Manish Verma', 'manish.verma@company.com', '9876543232', 'Devops', 'C1', 'Infrastructure', 'Active', NOW(), NOW()),
('EMP024', 'Ritika Sharma', 'ritika.sharma@company.com', '9876543233', 'Devops', 'C1', 'Infrastructure', 'Active', NOW(), NOW()),
('EMP025', 'Suresh Patel', 'suresh.patel@company.com', '9876543234', 'Devops', 'C1', 'Infrastructure', 'Active', NOW(), NOW());

-- DotNet Cohort 1 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP026', 'Ashok Kumar', 'ashok.kumar@company.com', '9876543235', 'DotNet', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP027', 'Isha Patel', 'isha.patel@company.com', '9876543236', 'DotNet', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP028', 'Vikram Desai', 'vikram.desai@company.com', '9876543237', 'DotNet', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP029', 'Priya Nair', 'priya.nair@company.com', '9876543238', 'DotNet', 'C1', 'Engineering', 'Active', NOW(), NOW()),
('EMP030', 'Arjun Singh', 'arjun.singh@company.com', '9876543239', 'DotNet', 'C1', 'Engineering', 'Active', NOW(), NOW());

-- SalesForce Cohort 1 Employees
INSERT IGNORE INTO employees (emp_id, name, email, phone, technology, cohort, department, status, created_at, updated_at) VALUES
('EMP031', 'Sanjana Reddy', 'sanjana.reddy@company.com', '9876543240', 'SalesForce', 'C1', 'CRM', 'Active', NOW(), NOW()),
('EMP032', 'Nitin Sharma', 'nitin.sharma@company.com', '9876543241', 'SalesForce', 'C1', 'CRM', 'Active', NOW(), NOW()),
('EMP033', 'Divya Sharma', 'divya.sharma@company.com', '9876543242', 'SalesForce', 'C1', 'CRM', 'Active', NOW(), NOW()),
('EMP034', 'Rohit Verma', 'rohit.verma@company.com', '9876543243', 'SalesForce', 'C1', 'CRM', 'Active', NOW(), NOW()),
('EMP035', 'Ananya Gupta', 'ananya.gupta@company.com', '9876543244', 'SalesForce', 'C1', 'CRM', 'Active', NOW(), NOW());
