package com.identra.canonical;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Create/update payload for canonical identities (tenant taken from request header). */
public record IdentityWriteRequest(
        @NotBlank String userName,
        List<Identity.Email> emails,
        Identity.Name name,
        @NotNull Boolean active,
        String department,
        UUID managerId,
        Identity.EmploymentStatus employmentStatus,
        List<Identity.ExternalId> externalIds,
        String sourceSystem,
        Map<String, Object> customAttributes
) {
}
