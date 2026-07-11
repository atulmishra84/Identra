package com.identra.adapter.spi;

import java.util.Set;

/** Declares what an adapter can do so Fabric can negotiate capabilities. */
public record CapabilitiesDescriptor(
        String adapterId,
        String vendor,
        String version,
        Set<Capability> capabilities
) {
    public enum Capability {
        IDENTITY_CRUD,
        SEARCH,
        ENTITLEMENT_ASSIGN,
        PASSWORD_SET,
        DISABLE,
        DELETE,
        DELTA_SYNC,
        FULL_SYNC
    }
}
