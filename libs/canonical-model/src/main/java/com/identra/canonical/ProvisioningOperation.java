package com.identra.canonical;

import java.util.Map;
import java.util.UUID;

public record ProvisioningOperation(
        OperationType type,
        UUID applicationId,
        UUID identityId,
        UUID accountId,
        Map<String, Object> attributes,
        java.util.List<UUID> entitlementIds
) {
    public enum OperationType {
        CREATE,
        UPDATE,
        DISABLE,
        DELETE,
        SET_PASSWORD,
        ASSIGN_ENTITLEMENT,
        REVOKE_ENTITLEMENT
    }
}
