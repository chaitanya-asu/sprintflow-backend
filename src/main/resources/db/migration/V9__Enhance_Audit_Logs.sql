-- V9: Enhance Audit Logs Table
-- Adds user role, IP address, old/new values, and performance indexes

-- Add new columns
ALTER TABLE audit_logs ADD COLUMN user_role VARCHAR(50);
ALTER TABLE audit_logs ADD COLUMN ip_address VARCHAR(45);
ALTER TABLE audit_logs ADD COLUMN old_value TEXT;
ALTER TABLE audit_logs ADD COLUMN new_value TEXT;

-- Add performance indexes
CREATE INDEX idx_audit_performed_by ON audit_logs(performed_by);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
