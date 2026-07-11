package com.identra.canonical;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;

/**
 * Canonical provisioning operation submitted to Identra.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProvisioningRequest(
        UUID tenantId,
        String idempotencyKey,
        Operation operation,
        UUID identityId,
        String applicationKey,
        Map<String, Object> attributes,
        Map<String, Object> entitlements
) {
    public enum Operation {
        CREATE,
        UPDATE,
        DISABLE,
        ENABLE,
        DELETE,
        RESET_PASSWORD,
        ASSIGN_ENTITLEMENT,
        REVOKE_ENTITLEMENT
    }
}
