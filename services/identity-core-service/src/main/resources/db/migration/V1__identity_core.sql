-- Identity Core lightweight IAM
CREATE TABLE IF NOT EXISTS core_identity (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    user_name VARCHAR(320) NOT NULL,
    email VARCHAR(320),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    department VARCHAR(255),
    manager_id UUID,
    lifecycle_state VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_core_identity_tenant_user UNIQUE (tenant_id, user_name)
);

CREATE TABLE IF NOT EXISTS core_role (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    CONSTRAINT uq_core_role UNIQUE (tenant_id, name)
);

CREATE TABLE IF NOT EXISTS core_role_assignment (
    tenant_id UUID NOT NULL,
    identity_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (tenant_id, identity_id, role_id)
);

CREATE TABLE IF NOT EXISTS core_access_request (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    identity_id UUID NOT NULL,
    role_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    decided_at TIMESTAMPTZ
);
