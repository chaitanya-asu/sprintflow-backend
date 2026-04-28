-- ============================================================================
-- SprintFlow Cohort Naming Migration
-- Updates all old cohort names (JC1, PC1, DC1, etc.) to new pattern (C1-C12)
-- ============================================================================

-- Backup before running (optional but recommended)
-- CREATE TABLE employees_backup AS SELECT * FROM employees;
-- CREATE TABLE sprints_backup AS SELECT * FROM sprints;

-- ============================================================================
-- EMPLOYEES TABLE
-- ============================================================================

-- Java cohorts
UPDATE employees SET cohort = 'C1' WHERE cohort = 'JC1' OR cohort = 'Java cohort 1';
UPDATE employees SET cohort = 'C2' WHERE cohort = 'JC2' OR cohort = 'Java cohort 2';
UPDATE employees SET cohort = 'C3' WHERE cohort = 'JC3';
UPDATE employees SET cohort = 'C4' WHERE cohort = 'JC4';

-- Python cohorts
UPDATE employees SET cohort = 'C4' WHERE cohort = 'PC1' OR cohort = 'Python cohort 1';
UPDATE employees SET cohort = 'C5' WHERE cohort = 'PC2' OR cohort = 'Python cohort 2';
UPDATE employees SET cohort = 'C6' WHERE cohort = 'PC3';

-- DevOps cohorts
UPDATE employees SET cohort = 'C7' WHERE cohort = 'DC1' OR cohort = 'DevOps cohort 1';
UPDATE employees SET cohort = 'C8' WHERE cohort = 'DC2';

-- .NET cohorts
UPDATE employees SET cohort = 'C9' WHERE cohort = 'DNC1' OR cohort = '.NET cohort 1';
UPDATE employees SET cohort = 'C10' WHERE cohort = 'DNC2';

-- Salesforce cohorts
UPDATE employees SET cohort = 'C11' WHERE cohort = 'SC1' OR cohort = 'Salesforce cohort 1';
UPDATE employees SET cohort = 'C12' WHERE cohort = 'SC2';

-- Legacy names
UPDATE employees SET cohort = 'C1' WHERE cohort = 'Cohort A' OR cohort = 'cohortA';
UPDATE employees SET cohort = 'C2' WHERE cohort = 'Cohort B' OR cohort = 'cohortB';
UPDATE employees SET cohort = 'C3' WHERE cohort = 'Cohort C' OR cohort = 'cohortC';

-- ============================================================================
-- SPRINTS TABLE
-- ============================================================================

-- Java cohorts
UPDATE sprints SET cohort = 'C1' WHERE cohort = 'JC1' OR cohort = 'Java cohort 1';
UPDATE sprints SET cohort = 'C2' WHERE cohort = 'JC2' OR cohort = 'Java cohort 2';
UPDATE sprints SET cohort = 'C3' WHERE cohort = 'JC3';
UPDATE sprints SET cohort = 'C4' WHERE cohort = 'JC4';

-- Python cohorts
UPDATE sprints SET cohort = 'C4' WHERE cohort = 'PC1' OR cohort = 'Python cohort 1';
UPDATE sprints SET cohort = 'C5' WHERE cohort = 'PC2' OR cohort = 'Python cohort 2';
UPDATE sprints SET cohort = 'C6' WHERE cohort = 'PC3';

-- DevOps cohorts
UPDATE sprints SET cohort = 'C7' WHERE cohort = 'DC1' OR cohort = 'DevOps cohort 1';
UPDATE sprints SET cohort = 'C8' WHERE cohort = 'DC2';

-- .NET cohorts
UPDATE sprints SET cohort = 'C9' WHERE cohort = 'DNC1' OR cohort = '.NET cohort 1';
UPDATE sprints SET cohort = 'C10' WHERE cohort = 'DNC2';

-- Salesforce cohorts
UPDATE sprints SET cohort = 'C11' WHERE cohort = 'SC1' OR cohort = 'Salesforce cohort 1';
UPDATE sprints SET cohort = 'C12' WHERE cohort = 'SC2';

-- Legacy names
UPDATE sprints SET cohort = 'C1' WHERE cohort = 'Cohort A' OR cohort = 'cohortA';
UPDATE sprints SET cohort = 'C2' WHERE cohort = 'Cohort B' OR cohort = 'cohortB';
UPDATE sprints SET cohort = 'C3' WHERE cohort = 'Cohort C' OR cohort = 'cohortC';

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Check employees cohorts
SELECT DISTINCT cohort FROM employees WHERE cohort IS NOT NULL ORDER BY cohort;

-- Check sprints cohorts
SELECT DISTINCT cohort FROM sprints WHERE cohort IS NOT NULL ORDER BY cohort;

-- Count by cohort
SELECT cohort, COUNT(*) as count FROM employees WHERE cohort IS NOT NULL GROUP BY cohort ORDER BY cohort;

-- Find any remaining old patterns (should be empty)
SELECT DISTINCT cohort FROM employees 
WHERE cohort IN ('JC1', 'JC2', 'JC3', 'JC4', 'PC1', 'PC2', 'PC3', 'DC1', 'DC2', 'DNC1', 'DNC2', 'SC1', 'SC2', 'Cohort A', 'Cohort B', 'Cohort C')
ORDER BY cohort;

-- ============================================================================
-- ROLLBACK (if needed)
-- ============================================================================
-- Uncomment to restore from backup:
-- DROP TABLE employees;
-- DROP TABLE sprints;
-- RENAME TABLE employees_backup TO employees;
-- RENAME TABLE sprints_backup TO sprints;
