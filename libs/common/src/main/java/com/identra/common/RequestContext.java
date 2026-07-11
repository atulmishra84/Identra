package com.identra.common;

import java.util.Objects;
import java.util.UUID;

/** Tenant-scoped correlation context propagated across services. */
public record RequestContext(UUID tenantId, String correlationId, String actorId) {

    public RequestContext {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(correlationId, "correlationId");
    }

    public static RequestContext of(UUID tenantId, String correlationId) {
        return new RequestContext(tenantId, correlationId, null);
    }
}
