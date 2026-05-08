-- ── 15. FIX TASK STATUS COLUMN ─────────────────────────────
-- Change status from ENUM('TODO','IN_PROGRESS','REVIEW','DONE','BLOCKED')
-- to VARCHAR(50) to match the TaskStatus enum values
-- (NOT_STARTED, IN_PROGRESS, NOT_SUBMITTED, CLOSED)
ALTER TABLE tasks MODIFY COLUMN status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED';
