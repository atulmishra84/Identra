package com.identra.security;

/**
 * Placeholder for JWT / tenant claim validation utilities (Sprint 1–3).
 */
public final class TenantClaims {

    private TenantClaims() {
    }

    public static final String TENANT_CLAIM = "tid";
    public static final String ROLES_CLAIM = "roles";
}
