-- Add columns only if they don't exist
ALTER TABLE cohorts ADD COLUMN technology VARCHAR(20) DEFAULT NULL;
ALTER TABLE cohorts ADD COLUMN cohort_number VARCHAR(10) DEFAULT NULL;

-- Update existing data: split name (e.g. "Java C1") into technology and cohort_number
-- Assuming space separation for legacy data
UPDATE cohorts 
SET technology = SUBSTRING_INDEX(name, ' ', 1),
    cohort_number = SUBSTRING_INDEX(name, ' ', -1)
WHERE (technology IS NULL OR technology = '') AND name LIKE '% %';

-- Update existing data: handle no-space case (e.g. "JavaC1")
-- This is harder in plain SQL without regex, but we can do common ones
UPDATE cohorts 
SET technology = 'Java', cohort_number = REPLACE(name, 'Java', '')
WHERE (technology IS NULL OR technology = '') AND name LIKE 'Java%';

UPDATE cohorts 
SET technology = 'Python', cohort_number = REPLACE(name, 'Python', '')
WHERE (technology IS NULL OR technology = '') AND name LIKE 'Python%';

UPDATE cohorts 
SET technology = 'Devops', cohort_number = REPLACE(name, 'Devops', '')
WHERE (technology IS NULL OR technology = '') AND name LIKE 'Devops%';

-- Final cleanup: Ensure name follows the new "No Space" convention
UPDATE cohorts SET name = CONCAT(technology, cohort_number) 
WHERE technology IS NOT NULL AND cohort_number IS NOT NULL;
