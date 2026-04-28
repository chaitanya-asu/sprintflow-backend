-- Relax the cohort pattern constraint to allow prefixed names like "Java C1"
-- The old strict constraint was manually removed or doesn't exist

ALTER TABLE employees 
ADD CONSTRAINT chk_cohort_pattern 
CHECK (cohort REGEXP '^(.* )?C[0-9]+$');

