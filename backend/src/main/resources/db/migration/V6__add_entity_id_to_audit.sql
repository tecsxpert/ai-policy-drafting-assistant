-- Add entity_id column to audit_log if it doesn't exist
ALTER TABLE audit_log 
ADD COLUMN IF NOT EXISTS entity_id VARCHAR(255) DEFAULT 'unknown';

-- Add missing indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_audit_entity 
ON audit_log(entity_type, entity_id);

CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_log(action);
CREATE INDEX IF NOT EXISTS idx_audit_changed_at ON audit_log(changed_at);
CREATE INDEX IF NOT EXISTS idx_audit_changed_by ON audit_log(changed_by);
