package com.identra.tenant.model;

import java.time.Instant;
import java.util.UUID;

public record Tenant(
        UUID id,
        String name,
        String plan,
        String deploymentProfile,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
