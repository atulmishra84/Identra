package com.identra.adapter.okta;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Okta Tier-A adapter — calls Okta Users API (or dry-run mode for local MVP demos).
 */
public class OktaIdentityAdapter implements IdentityAdapter, HealthCapable {

    public static final String ADAPTER_ID = "okta";

    private final OktaClient client;
    private final UUID defaultTenantId;

    public OktaIdentityAdapter() {
        this(OktaClientConfig.dryRunDefaults(), UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    public OktaIdentityAdapter(OktaClientConfig config, UUID defaultTenantId) {
        this.client = new OktaClient(config);
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                ADAPTER_ID,
                "Okta",
                "0.2.0-SNAPSHOT",
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
    public Optional<Identity> getByExternalId(String externalId) {
        return client.getUser(externalId).map(user -> client.toCanonical(user, defaultTenantId));
    }

    @Override
    public Identity create(Identity identity) {
        Map<String, Object> created = client.createUser(identity);
        return client.toCanonical(created, identity.tenantId() == null ? defaultTenantId : identity.tenantId());
    }

    @Override
    public Identity update(Identity identity) {
        String externalId = externalId(identity)
                .orElseThrow(() -> new IllegalArgumentException("Okta externalId required for update"));
        Map<String, Object> updated = client.updateUser(externalId, identity);
        return client.toCanonical(updated, identity.tenantId() == null ? defaultTenantId : identity.tenantId());
    }

    @Override
    public void disable(String externalId) {
        client.deactivate(externalId);
    }

    @Override
    public void delete(String externalId) {
        client.delete(externalId);
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        return client.search(filter, count).stream()
                .map(user -> client.toCanonical(user, defaultTenantId))
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
                .filter(e -> ADAPTER_ID.equalsIgnoreCase(e.system()))
                .map(Identity.ExternalId::value)
                .findFirst();
    }
}
