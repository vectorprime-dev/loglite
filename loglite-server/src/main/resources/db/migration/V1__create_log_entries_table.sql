CREATE TABLE log_entries (
    id           UUID PRIMARY KEY,
    timestamp    TIMESTAMPTZ  NOT NULL,
    logger_name  VARCHAR(255),
    level        VARCHAR(255) NOT NULL,
    message      VARCHAR(8192),
    thread_name  VARCHAR(255),
    metadata     JSONB
);
