-- ═══════════════════════════════════════════════════════════════
-- SprintFlow — Cohort Separation Migration
-- Separates cohort name into technology and cohort_number
-- ═══════════════════════════════════════════════════════════════

USE sprintflow_db;

-- ── 1. Add new columns to cohorts table ──────────────────────
ALTER TABLE cohorts
ADD COLUMN technology VARCHAR(20) AFTER patternType,
ADD COLUMN cohort_number VARCHAR(10) AFTER technology;

-- ── 2. Populate technology and cohort_number from existing names ──
-- Pattern: "Java C1" → technology="Java", cohort_number="C1"
UPDATE cohorts
SET 
  technology = TRIM(SUBSTRING_INDEX(name, ' ', 1)),
  cohort_number = TRIM(SUBSTRING_INDEX(name, ' ', -1))
WHERE name IS NOT NULL AND name != '';

-- ── 3. Verify the migration ──────────────────────────────────
SELECT id, name, patternType, technology, cohort_number FROM cohorts LIMIT 10;

-- ── 4. Add indexes for better query performance ──────────────
ALTER TABLE cohorts
ADD INDEX idx_cohorts_tech_num (technology, cohort_number),
ADD INDEX idx_cohorts_tech (technology);

-- ── 5. Optional: Add constraint to ensure technology matches patternType ──
-- This ensures data consistency
ALTER TABLE cohorts
ADD CONSTRAINT chk_cohort_tech_match 
CHECK (LOWER(technology) = LOWER(patternType));

-- ── 6. Verify counts ─────────────────────────────────────────
SELECT 
  COUNT(*) as total_cohorts,
  COUNT(DISTINCT technology) as unique_technologies,
  COUNT(DISTINCT cohort_number) as unique_cohort_numbers
FROM cohorts;

-- ── 7. Show sample data ──────────────────────────────────────
SELECT 
  id, 
  name, 
  patternType, 
  technology, 
  cohort_number, 
  status 
FROM cohorts 
ORDER BY technology, cohort_number;
