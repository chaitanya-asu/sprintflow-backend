-- ── 14. MAKE TASKS.SPRINT_ID NULLABLE ────────────────────────────
-- Tasks can be created without a sprint association
ALTER TABLE tasks MODIFY COLUMN sprint_id BIGINT NULL;
