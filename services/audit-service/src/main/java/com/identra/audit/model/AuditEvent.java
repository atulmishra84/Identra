package com.identra.audit.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID id,
        UUID tenantId,
        String eventType,
        String actor,
        String resourceType,
        String resourceId,
        String message,
        Map<String, Object> payload,
        Instant occurredAt
) {
}
