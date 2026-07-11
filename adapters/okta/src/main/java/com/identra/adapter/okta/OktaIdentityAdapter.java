package com.identra.adapter.okta;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Okta Tier-A adapter stub (MVP).
 * Vendor SDK calls will be implemented in sprints 4–6.
 */
public class OktaIdentityAdapter implements IdentityAdapter, HealthCapable {

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                "okta",
                "Okta",
                "0.1.0-SNAPSHOT",
                EnumSet.of(
                        CapabilitiesDescriptor.Capability.IDENTITY_CRUD,
                        CapabilitiesDescriptor.Capability.SEARCH,
                        CapabilitiesDescriptor.Capability.ENTITLEMENT_ASSIGN,
                        CapabilitiesDescriptor.Capability.PASSWORD_SET,
                        CapabilitiesDescriptor.Capability.DISABLE,
                        CapabilitiesDescriptor.Capability.DELETE,
                        CapabilitiesDescriptor.Capability.DELTA_SYNC
                )
        );
    }

    @Override
    public Optional<Identity> get(UUID identityId) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public Identity create(Identity identity) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public Identity update(Identity identity) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public void disable(UUID identityId) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public void delete(UUID identityId) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        throw new UnsupportedOperationException("Okta adapter not yet connected — Sprint 4–6");
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UNKNOWN;
    }
}
