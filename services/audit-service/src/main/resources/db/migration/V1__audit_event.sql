-- Audit event store
CREATE TABLE IF NOT EXISTS audit_event (
    id              UUID PRIMARY KEY,
    tenant_id       UUID NOT NULL,
    event_type      VARCHAR(128) NOT NULL,
    actor           VARCHAR(255),
    resource_type   VARCHAR(128),
    resource_id     VARCHAR(255),
    message         TEXT NOT NULL,
    payload_json    JSONB NOT NULL DEFAULT '{}'::jsonb,
    occurred_at     TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_tenant_time ON audit_event (tenant_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_type ON audit_event (tenant_id, event_type);
