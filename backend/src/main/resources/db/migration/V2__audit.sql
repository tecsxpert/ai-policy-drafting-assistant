CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,

    action VARCHAR(50) NOT NULL,

    old_data JSONB,
    new_data JSONB,

    changed_by VARCHAR(100),

    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Composite index
CREATE INDEX idx_audit_entity 
ON audit_log(entity_type, entity_id);

-- Additional indexes
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_changed_at ON audit_log(changed_at);
CREATE INDEX idx_audit_changed_by ON audit_log(changed_by);