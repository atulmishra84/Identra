package com.identra.canonical;

import java.util.UUID;

public record Entitlement(
        UUID id,
        UUID applicationId,
        String name,
        String description,
        String type
) {}
