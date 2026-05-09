CREATE INDEX idx_policies_deadline_deleted
ON policies(due_date, is_deleted);

CREATE INDEX idx_policies_title
ON policies(title);

CREATE INDEX idx_policies_category
ON policies(category);