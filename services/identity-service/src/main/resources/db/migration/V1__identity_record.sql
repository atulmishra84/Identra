-- Identra identity schema (Sprint 1)
CREATE TABLE IF NOT EXISTS identity_record (
    id              UUID PRIMARY KEY,
    tenant_id       UUID NOT NULL,
    user_name       VARCHAR(320) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    department      VARCHAR(255),
    manager_id      UUID,
    employment_status VARCHAR(32),
    source_system   VARCHAR(128),
    emails_json     JSONB NOT NULL DEFAULT '[]'::jsonb,
    name_json       JSONB,
    external_ids_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    custom_attributes_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    version         BIGINT NOT NULL DEFAULT 0,
    etag            VARCHAR(64) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_identity_tenant_username UNIQUE (tenant_id, user_name)
);

CREATE INDEX IF NOT EXISTS idx_identity_tenant ON identity_record (tenant_id);
CREATE INDEX IF NOT EXISTS idx_identity_tenant_active ON identity_record (tenant_id, active);
