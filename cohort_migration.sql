-- ═══════════════════════════════════════════════════════════════════════════════
-- SprintFlow — COHORT NAMING MIGRATION
-- Updates all cohort names to standardized format: JavaC1, JavaC2, PythonC1, etc.
-- ═══════════════════════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── BACKUP (Optional) ──────────────────────────────────────────────────────────
-- CREATE TABLE employees_backup AS SELECT * FROM employees;
-- CREATE TABLE sprints_backup AS SELECT * FROM sprints;

-- ── UPDATE EMPLOYEES TABLE ────────────────────────────────────────────────────

-- Java cohorts
UPDATE employees SET cohort = 'JavaC1' WHERE cohort IN ('JC1', 'Java cohort 1', 'C1', 'Cohort A', 'cohortA') AND technology = 'Java';
UPDATE employees SET cohort = 'JavaC2' WHERE cohort IN ('JC2', 'Java cohort 2', 'C2', 'Cohort B', 'cohortB') AND technology = 'Java';
UPDATE employees SET cohort = 'JavaC3' WHERE cohort IN ('JC3', 'Java cohort 3', 'C3', 'Cohort C', 'cohortC') AND technology = 'Java';
UPDATE employees SET cohort = 'JavaC4' WHERE cohort IN ('JC4', 'Java cohort 4', 'C4') AND technology = 'Java';

-- Python cohorts
UPDATE employees SET cohort = 'PythonC1' WHERE cohort IN ('PC1', 'Python cohort 1', 'C4') AND technology = 'Python';
UPDATE employees SET cohort = 'PythonC2' WHERE cohort IN ('PC2', 'Python cohort 2', 'C5') AND technology = 'Python';
UPDATE employees SET cohort = 'PythonC3' WHERE cohort IN ('PC3', 'Python cohort 3', 'C6') AND technology = 'Python';

-- DevOps cohorts
UPDATE employees SET cohort = 'DevopsC1' WHERE cohort IN ('DC1', 'DevOps cohort 1', 'C7') AND technology = 'Devops';
UPDATE employees SET cohort = 'DevopsC2' WHERE cohort IN ('DC2', 'DevOps cohort 2', 'C8') AND technology = 'Devops';

-- .NET cohorts
UPDATE employees SET cohort = 'DotNetC1' WHERE cohort IN ('DNC1', '.NET cohort 1', 'NC1', 'C9') AND technology = 'DotNet';
UPDATE employees SET cohort = 'DotNetC2' WHERE cohort IN ('DNC2', '.NET cohort 2', 'NC2', 'C10') AND technology = 'DotNet';

-- Salesforce cohorts
UPDATE employees SET cohort = 'SalesForceC1' WHERE cohort IN ('SC1', 'Salesforce cohort 1', 'C11') AND technology = 'SalesForce';
UPDATE employees SET cohort = 'SalesForceC2' WHERE cohort IN ('SC2', 'Salesforce cohort 2', 'C12') AND technology = 'SalesForce';

-- ── UPDATE SPRINTS TABLE ──────────────────────────────────────────────────────

-- Java sprints
UPDATE sprints SET cohort = 'JavaC1' WHERE cohort IN ('JC1', 'Java cohort 1', 'C1', 'Cohort A', 'cohortA') AND technology = 'Java';
UPDATE sprints SET cohort = 'JavaC2' WHERE cohort IN ('JC2', 'Java cohort 2', 'C2', 'Cohort B', 'cohortB') AND technology = 'Java';
UPDATE sprints SET cohort = 'JavaC3' WHERE cohort IN ('JC3', 'Java cohort 3', 'C3', 'Cohort C', 'cohortC') AND technology = 'Java';
UPDATE sprints SET cohort = 'JavaC4' WHERE cohort IN ('JC4', 'Java cohort 4', 'C4') AND technology = 'Java';

-- Python sprints
UPDATE sprints SET cohort = 'PythonC1' WHERE cohort IN ('PC1', 'Python cohort 1', 'C4') AND technology = 'Python';
UPDATE sprints SET cohort = 'PythonC2' WHERE cohort IN ('PC2', 'Python cohort 2', 'C5') AND technology = 'Python';
UPDATE sprints SET cohort = 'PythonC3' WHERE cohort IN ('PC3', 'Python cohort 3', 'C6') AND technology = 'Python';

-- DevOps sprints
UPDATE sprints SET cohort = 'DevopsC1' WHERE cohort IN ('DC1', 'DevOps cohort 1', 'C7') AND technology = 'Devops';
UPDATE sprints SET cohort = 'DevopsC2' WHERE cohort IN ('DC2', 'DevOps cohort 2', 'C8') AND technology = 'Devops';

-- .NET sprints
UPDATE sprints SET cohort = 'DotNetC1' WHERE cohort IN ('DNC1', '.NET cohort 1', 'NC1', 'C9') AND technology = 'DotNet';
UPDATE sprints SET cohort = 'DotNetC2' WHERE cohort IN ('DNC2', '.NET cohort 2', 'NC2', 'C10') AND technology = 'DotNet';

-- Salesforce sprints
UPDATE sprints SET cohort = 'SalesForceC1' WHERE cohort IN ('SC1', 'Salesforce cohort 1', 'C11') AND technology = 'SalesForce';
UPDATE sprints SET cohort = 'SalesForceC2' WHERE cohort IN ('SC2', 'Salesforce cohort 2', 'C12') AND technology = 'SalesForce';

-- ── UPDATE COHORTS_JSON IN SPRINTS ────────────────────────────────────────────

-- Java cohorts in JSON
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"JC1"', '"cohort":"JavaC1"') WHERE technology = 'Java';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"JC2"', '"cohort":"JavaC2"') WHERE technology = 'Java';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C1"', '"cohort":"JavaC1"') WHERE technology = 'Java' AND cohorts_json LIKE '%"technology":"Java"%';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C2"', '"cohort":"JavaC2"') WHERE technology = 'Java' AND cohorts_json LIKE '%"technology":"Java"%';

-- Python cohorts in JSON
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"PC1"', '"cohort":"PythonC1"') WHERE technology = 'Python';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"PC2"', '"cohort":"PythonC2"') WHERE technology = 'Python';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C4"', '"cohort":"PythonC1"') WHERE technology = 'Python' AND cohorts_json LIKE '%"technology":"Python"%';

-- DevOps cohorts in JSON
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"DC1"', '"cohort":"DevopsC1"') WHERE technology = 'Devops';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C7"', '"cohort":"DevopsC1"') WHERE technology = 'Devops' AND cohorts_json LIKE '%"technology":"Devops"%';

-- .NET cohorts in JSON
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"DNC1"', '"cohort":"DotNetC1"') WHERE technology = 'DotNet';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"NC1"', '"cohort":"DotNetC1"') WHERE technology = 'DotNet';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C9"', '"cohort":"DotNetC1"') WHERE technology = 'DotNet' AND cohorts_json LIKE '%"technology":"DotNet"%';

-- Salesforce cohorts in JSON
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"SC1"', '"cohort":"SalesForceC1"') WHERE technology = 'SalesForce';
UPDATE sprints SET cohorts_json = REPLACE(cohorts_json, '"cohort":"C11"', '"cohort":"SalesForceC1"') WHERE technology = 'SalesForce' AND cohorts_json LIKE '%"technology":"SalesForce"%';

-- ── VERIFICATION QUERIES ──────────────────────────────────────────────────────

SELECT '=== MIGRATION COMPLETE ===' AS status;

SELECT '=== EMPLOYEES BY COHORT ===' AS info;
SELECT technology, cohort, COUNT(*) as count FROM employees GROUP BY technology, cohort ORDER BY technology, cohort;

SELECT '=== SPRINTS ===' AS info;
SELECT id, title, technology, cohort FROM sprints ORDER BY id;

SELECT '=== SPRINTS WITH COHORTS_JSON ===' AS info;
SELECT id, title, technology, cohort, cohorts_json FROM sprints WHERE cohorts_json IS NOT NULL ORDER BY id;

-- ── FIND ANY REMAINING OLD PATTERNS (should be empty) ──────────────────────────

SELECT '=== CHECKING FOR OLD PATTERNS ===' AS info;
SELECT DISTINCT cohort FROM employees 
WHERE cohort IN ('JC1', 'JC2', 'JC3', 'JC4', 'PC1', 'PC2', 'PC3', 'DC1', 'DC2', 'DNC1', 'DNC2', 'SC1', 'SC2', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6', 'C7', 'C8', 'C9', 'C10', 'C11', 'C12', 'Cohort A', 'Cohort B', 'Cohort C')
ORDER BY cohort;

-- ── ROLLBACK (if needed) ──────────────────────────────────────────────────────
-- Uncomment to restore from backup:
-- DROP TABLE employees;
-- DROP TABLE sprints;
-- RENAME TABLE employees_backup TO employees;
-- RENAME TABLE sprints_backup TO sprints;
