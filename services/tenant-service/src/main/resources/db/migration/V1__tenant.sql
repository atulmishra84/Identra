-- Tenant registry
CREATE TABLE IF NOT EXISTS tenant (
    id                  UUID PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    plan                VARCHAR(64) NOT NULL DEFAULT 'mvp',
    deployment_profile  VARCHAR(64) NOT NULL DEFAULT 'saas-shared',
    status              VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_tenant_name UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_tenant_status ON tenant (status);
