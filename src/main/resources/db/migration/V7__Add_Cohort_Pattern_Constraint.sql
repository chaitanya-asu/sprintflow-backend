-- Add CHECK constraint to enforce cohort naming pattern (C1, C2, C3, etc.)
-- This prevents old patterns (JC1, PC1, DC1) from being inserted
ALTER TABLE employees 
ADD CONSTRAINT chk_cohort_pattern 
CHECK (cohort REGEXP '^C[0-9]+$');
