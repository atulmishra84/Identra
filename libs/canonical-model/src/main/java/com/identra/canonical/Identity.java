package com.identra.canonical;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical identity — vendor-neutral. No vendor types allowed here.
 */
public record Identity(
        UUID id,
        UUID tenantId,
        List<ExternalId> externalIds,
        String userName,
        List<Email> emails,
        Name name,
        boolean active,
        String department,
        UUID managerId,
        EmploymentStatus employmentStatus,
        Map<String, Object> customAttributes,
        String sourceSystem,
        long version,
        String etag,
        Instant createdAt,
        Instant updatedAt
) {
    public enum EmploymentStatus {
        ACTIVE,
        LEAVE,
        TERMINATED,
        CONTRACTOR
    }

    public record ExternalId(String system, String value) {
    }

    public record Email(String value, String type, boolean primary) {
    }

    public record Name(String formatted, String familyName, String givenName) {
    }
}
