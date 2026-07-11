package com.identra.security;

/** Platform RBAC roles for the Identra control plane. */
public enum PlatformRole {
    TENANT_ADMIN,
    IAM_OPERATOR,
    CONNECTOR_DEVELOPER,
    AUDITOR,
    APPROVER,
    MARKETPLACE_PUBLISHER,
    READ_ONLY
}
