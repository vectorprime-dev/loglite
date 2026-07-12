CREATE INDEX idx_log_entries_metadata_gin ON log_entries USING GIN (metadata jsonb_path_ops);
