package com.identra.adapter.sailpoint.isc;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SailPoint Identity Security Cloud (ISC) Tier-A adapter stub (MVP).
 * Vendor API calls will be implemented in sprints 7–9.
 */
public class SailPointIscIdentityAdapter implements IdentityAdapter, HealthCapable {

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                "sailpoint-isc",
                "SailPoint ISC",
                "0.1.0-SNAPSHOT",
                EnumSet.of(
                        CapabilitiesDescriptor.Capability.IDENTITY_CRUD,
                        CapabilitiesDescriptor.Capability.SEARCH,
                        CapabilitiesDescriptor.Capability.ENTITLEMENT_ASSIGN,
                        CapabilitiesDescriptor.Capability.PASSWORD_SET,
                        CapabilitiesDescriptor.Capability.DISABLE,
                        CapabilitiesDescriptor.Capability.DELETE,
                        CapabilitiesDescriptor.Capability.FULL_SYNC,
                        CapabilitiesDescriptor.Capability.DELTA_SYNC
                )
        );
    }

    @Override
    public Optional<Identity> get(UUID identityId) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public Identity create(Identity identity) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public Identity update(Identity identity) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public void disable(UUID identityId) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public void delete(UUID identityId) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        throw new UnsupportedOperationException("SailPoint ISC adapter not yet connected — Sprint 7–9");
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UNKNOWN;
    }
}
