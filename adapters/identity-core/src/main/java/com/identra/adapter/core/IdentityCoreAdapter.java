package com.identra.adapter.core;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Identity Core adapter stub — Bootstrap IAM implementing the same SPI (ADR-0008).
 * Full Identity Core module ships in v2.
 */
public class IdentityCoreAdapter implements IdentityAdapter, HealthCapable {

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                "identity-core",
                "Identra Identity Core",
                "0.1.0-SNAPSHOT",
                EnumSet.of(
                        CapabilitiesDescriptor.Capability.IDENTITY_CRUD,
                        CapabilitiesDescriptor.Capability.SEARCH,
                        CapabilitiesDescriptor.Capability.ENTITLEMENT_ASSIGN,
                        CapabilitiesDescriptor.Capability.PASSWORD_SET,
                        CapabilitiesDescriptor.Capability.DISABLE,
                        CapabilitiesDescriptor.Capability.DELETE
                )
        );
    }

    @Override
    public Optional<Identity> get(UUID identityId) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public Identity create(Identity identity) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public Identity update(Identity identity) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public void disable(UUID identityId) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public void delete(UUID identityId) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        throw new UnsupportedOperationException("Identity Core not implemented — v2");
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UNKNOWN;
    }
}
