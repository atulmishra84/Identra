package com.identra.adapter.sailpoint.isc;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SailPointIscIdentityAdapter implements IdentityAdapter, HealthCapable {

    public static final String ADAPTER_ID = "sailpoint-isc";

    private final SailPointIscClient client;
    private final UUID defaultTenantId;

    public SailPointIscIdentityAdapter() {
        this(SailPointIscClientConfig.dryRunDefaults(), UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    public SailPointIscIdentityAdapter(SailPointIscClientConfig config, UUID defaultTenantId) {
        this.client = new SailPointIscClient(config);
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                ADAPTER_ID,
                "SailPoint ISC",
                "0.2.0-SNAPSHOT",
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
    public Optional<Identity> getByExternalId(String externalId) {
        return client.getIdentity(externalId).map(i -> client.toCanonical(i, defaultTenantId));
    }

    @Override
    public Identity create(Identity identity) {
        Map<String, Object> created = client.createIdentity(identity);
        return client.toCanonical(created, identity.tenantId() == null ? defaultTenantId : identity.tenantId());
    }

    @Override
    public Identity update(Identity identity) {
        String externalId = externalId(identity)
                .orElseThrow(() -> new IllegalArgumentException("ISC externalId required for update"));
        Map<String, Object> updated = client.updateIdentity(externalId, identity);
        return client.toCanonical(updated, identity.tenantId() == null ? defaultTenantId : identity.tenantId());
    }

    @Override
    public void disable(String externalId) {
        client.disable(externalId);
    }

    @Override
    public void delete(String externalId) {
        client.delete(externalId);
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        return client.search(filter, count).stream()
                .map(i -> client.toCanonical(i, defaultTenantId))
                .toList();
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UP;
    }

    private static Optional<String> externalId(Identity identity) {
        if (identity.externalIds() == null) {
            return Optional.empty();
        }
        return identity.externalIds().stream()
                .filter(e -> ADAPTER_ID.equalsIgnoreCase(e.system()) || "isc".equalsIgnoreCase(e.system()))
                .map(Identity.ExternalId::value)
                .findFirst();
    }
}
