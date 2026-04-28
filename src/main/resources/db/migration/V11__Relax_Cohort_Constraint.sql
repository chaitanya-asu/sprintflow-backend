-- Relax the cohort pattern constraint to allow prefixed names like "Java C1"
-- Dropping the old strict constraint and adding a more flexible one
ALTER TABLE employees DROP CONSTRAINT IF EXISTS chk_cohort_pattern;

ALTER TABLE employees 
ADD CONSTRAINT chk_cohort_pattern 
CHECK (cohort REGEXP '^(.* )?C[0-9]+$');
