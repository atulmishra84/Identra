package com.identra.security;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record IdentraPrincipal(
        String subject,
        UUID tenantId,
        List<String> roles
) {
    public IdentraPrincipal {
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(tenantId, "tenantId");
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }
}
