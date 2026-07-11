package com.identra.canonical;

import java.time.Instant;
import java.util.UUID;

public record ProvisioningJob(
        UUID id,
        UUID tenantId,
        String status,
        ProvisioningRequest.Operation operation,
        UUID identityId,
        String applicationKey,
        String correlationId,
        String errorDetail,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt
) {
}
