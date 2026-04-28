-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Cohort Standardization Migration
-- Converts all legacy cohort formats to standardized C1-C12 format
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── MAPPING TABLE ─────────────────────────────────────────────
-- Legacy Format → New Format
-- JC1, JC2, JC3, JC4 → C1, C2, C3, C4 (Java)
-- PC1, PC2, PC3, PC4, PC5, PC6 → C5, C6, C7, C8, C9, C10 (Python)
-- DC1, DC2 → C11, C12 (Devops)
-- Cohort 1, Cohort 2, etc. → C1, C2, etc.

-- ── 1. UPDATE EMPLOYEES TABLE ─────────────────────────────────

-- Java cohorts: JC1 → C1, JC2 → C2, JC3 → C3, JC4 → C4
UPDATE employees SET cohort = 'C1' WHERE cohort = 'JC1';
UPDATE employees SET cohort = 'C2' WHERE cohort = 'JC2';
UPDATE employees SET cohort = 'C3' WHERE cohort = 'JC3';
UPDATE employees SET cohort = 'C4' WHERE cohort = 'JC4';

-- Python cohorts: PC1 → C5, PC2 → C6, PC3 → C7, PC4 → C8, PC5 → C9, PC6 → C10
UPDATE employees SET cohort = 'C5' WHERE cohort = 'PC1';
UPDATE employees SET cohort = 'C6' WHERE cohort = 'PC2';
UPDATE employees SET cohort = 'C7' WHERE cohort = 'PC3';
UPDATE employees SET cohort = 'C8' WHERE cohort = 'PC4';
UPDATE employees SET cohort = 'C9' WHERE cohort = 'PC5';
UPDATE employees SET cohort = 'C10' WHERE cohort = 'PC6';

-- Devops cohorts: DC1 → C11, DC2 → C12
UPDATE employees SET cohort = 'C11' WHERE cohort = 'DC1';
UPDATE employees SET cohort = 'C12' WHERE cohort = 'DC2';

-- Text format: Cohort 1 → C1, Cohort 2 → C2, etc.
UPDATE employees SET cohort = 'C1' WHERE cohort = 'Cohort 1';
UPDATE employees SET cohort = 'C2' WHERE cohort = 'Cohort 2';
UPDATE employees SET cohort = 'C3' WHERE cohort = 'Cohort 3';
UPDATE employees SET cohort = 'C4' WHERE cohort = 'Cohort 4';
UPDATE employees SET cohort = 'C5' WHERE cohort = 'Cohort 5';
UPDATE employees SET cohort = 'C6' WHERE cohort = 'Cohort 6';
UPDATE employees SET cohort = 'C7' WHERE cohort = 'Cohort 7';
UPDATE employees SET cohort = 'C8' WHERE cohort = 'Cohort 8';
UPDATE employees SET cohort = 'C9' WHERE cohort = 'Cohort 9';
UPDATE employees SET cohort = 'C10' WHERE cohort = 'Cohort 10';
UPDATE employees SET cohort = 'C11' WHERE cohort = 'Cohort 11';
UPDATE employees SET cohort = 'C12' WHERE cohort = 'Cohort 12';

-- ── 2. UPDATE SPRINTS TABLE ───────────────────────────────────

-- Update primary cohort field
UPDATE sprints SET cohort = 'C1' WHERE cohort = 'JC1';
UPDATE sprints SET cohort = 'C2' WHERE cohort = 'JC2';
UPDATE sprints SET cohort = 'C3' WHERE cohort = 'JC3';
UPDATE sprints SET cohort = 'C4' WHERE cohort = 'JC4';
UPDATE sprints SET cohort = 'C5' WHERE cohort = 'PC1';
UPDATE sprints SET cohort = 'C6' WHERE cohort = 'PC2';
UPDATE sprints SET cohort = 'C7' WHERE cohort = 'PC3';
UPDATE sprints SET cohort = 'C8' WHERE cohort = 'PC4';
UPDATE sprints SET cohort = 'C9' WHERE cohort = 'PC5';
UPDATE sprints SET cohort = 'C10' WHERE cohort = 'PC6';
UPDATE sprints SET cohort = 'C11' WHERE cohort = 'DC1';
UPDATE sprints SET cohort = 'C12' WHERE cohort = 'DC2';
UPDATE sprints SET cohort = 'C1' WHERE cohort = 'Cohort 1';
UPDATE sprints SET cohort = 'C2' WHERE cohort = 'Cohort 2';
UPDATE sprints SET cohort = 'C3' WHERE cohort = 'Cohort 3';
UPDATE sprints SET cohort = 'C4' WHERE cohort = 'Cohort 4';
UPDATE sprints SET cohort = 'C5' WHERE cohort = 'Cohort 5';
UPDATE sprints SET cohort = 'C6' WHERE cohort = 'Cohort 6';
UPDATE sprints SET cohort = 'C7' WHERE cohort = 'Cohort 7';
UPDATE sprints SET cohort = 'C8' WHERE cohort = 'Cohort 8';
UPDATE sprints SET cohort = 'C9' WHERE cohort = 'Cohort 9';
UPDATE sprints SET cohort = 'C10' WHERE cohort = 'Cohort 10';
UPDATE sprints SET cohort = 'C11' WHERE cohort = 'Cohort 11';
UPDATE sprints SET cohort = 'C12' WHERE cohort = 'Cohort 12';

-- Update cohorts_json field (multi-cohort sprints)
-- This requires JSON manipulation - update each cohort reference in the JSON array
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C1') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'JC1'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C2') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'JC2'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C3') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'JC3'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C4') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'JC4'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C5') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC1'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C6') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC2'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C7') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC3'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C8') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC4'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C9') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC5'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C10') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'PC6'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C11') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'DC1'));
UPDATE sprints SET cohorts_json = JSON_REPLACE(cohorts_json, '$[*].cohort', 'C12') 
  WHERE JSON_CONTAINS(cohorts_json, JSON_OBJECT('cohort', 'DC2'));

-- ── 3. VERIFY MIGRATION ───────────────────────────────────────

-- Check employees cohort distribution
SELECT 'Employees by Cohort' AS report;
SELECT cohort, COUNT(*) as count FROM employees GROUP BY cohort ORDER BY cohort;

-- Check sprints cohort distribution
SELECT 'Sprints by Cohort' AS report;
SELECT cohort, COUNT(*) as count FROM sprints WHERE cohort IS NOT NULL GROUP BY cohort ORDER BY cohort;

-- Check for any remaining legacy formats
SELECT 'Legacy Formats Still Present' AS report;
SELECT DISTINCT cohort FROM employees 
WHERE cohort IN ('JC1', 'JC2', 'JC3', 'JC4', 'PC1', 'PC2', 'PC3', 'PC4', 'PC5', 'PC6', 'DC1', 'DC2', 'Cohort 1', 'Cohort 2', 'Cohort 3', 'Cohort 4', 'Cohort 5', 'Cohort 6', 'Cohort 7', 'Cohort 8', 'Cohort 9', 'Cohort 10', 'Cohort 11', 'Cohort 12')
UNION
SELECT DISTINCT cohort FROM sprints 
WHERE cohort IN ('JC1', 'JC2', 'JC3', 'JC4', 'PC1', 'PC2', 'PC3', 'PC4', 'PC5', 'PC6', 'DC1', 'DC2', 'Cohort 1', 'Cohort 2', 'Cohort 3', 'Cohort 4', 'Cohort 5', 'Cohort 6', 'Cohort 7', 'Cohort 8', 'Cohort 9', 'Cohort 10', 'Cohort 11', 'Cohort 12');

-- Final summary
SELECT 'Final Summary' AS report;
SELECT 'Total Employees' as metric, COUNT(*) as value FROM employees
UNION ALL
SELECT 'Total Sprints', COUNT(*) FROM sprints
UNION ALL
SELECT 'Unique Cohorts', COUNT(DISTINCT cohort) FROM employees;
