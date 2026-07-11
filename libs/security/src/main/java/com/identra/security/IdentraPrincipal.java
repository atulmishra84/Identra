package com.identra.security;

import java.util.Set;
import java.util.UUID;

/** Authenticated principal extracted from JWT / mTLS. */
public record IdentraPrincipal(
        UUID tenantId,
        String subject,
        Set<PlatformRole> roles
) {}
