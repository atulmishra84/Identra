package com.identra.adapter.saviynt;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Saviynt EIC adapter (v2) — dry-run provisioning surface. */
public class SaviyntIdentityAdapter implements IdentityAdapter, HealthCapable {

    public static final String ADAPTER_ID = "saviynt";
    private static final Logger log = LoggerFactory.getLogger(SaviyntIdentityAdapter.class);

    private final UUID defaultTenantId;

    public SaviyntIdentityAdapter() {
        this(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    public SaviyntIdentityAdapter(UUID defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                ADAPTER_ID,
                "Saviynt",
                "0.1.0-SNAPSHOT",
                EnumSet.of(
                        CapabilitiesDescriptor.Capability.IDENTITY_CRUD,
                        CapabilitiesDescriptor.Capability.SEARCH,
                        CapabilitiesDescriptor.Capability.ENTITLEMENT_ASSIGN,
                        CapabilitiesDescriptor.Capability.DISABLE,
                        CapabilitiesDescriptor.Capability.DELETE,
                        CapabilitiesDescriptor.Capability.FULL_SYNC
                )
        );
    }

    @Override
    public Optional<Identity> getByExternalId(String externalId) {
        return Optional.of(canonical(externalId, "saviynt.user"));
    }

    @Override
    public Identity create(Identity identity) {
        log.info("Saviynt dry-run create {}", identity.userName());
        return canonical("sav-dry-" + UUID.randomUUID(), identity.userName());
    }

    @Override
    public Identity update(Identity identity) {
        log.info("Saviynt dry-run update {}", identity.userName());
        return identity;
    }

    @Override
    public void disable(String externalId) {
        log.info("Saviynt dry-run disable {}", externalId);
    }

    @Override
    public void delete(String externalId) {
        log.info("Saviynt dry-run delete {}", externalId);
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        return List.of(canonical("sav-dry-1", "saviynt.user"));
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UP;
    }

    private Identity canonical(String externalId, String userName) {
        Instant now = Instant.now();
        return new Identity(
                UUID.nameUUIDFromBytes(("saviynt:" + externalId).getBytes()),
                defaultTenantId,
                List.of(new Identity.ExternalId(ADAPTER_ID, externalId)),
                userName,
                List.of(),
                null,
                true,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of("saviyntApi", "/ECM/api/v5"),
                ADAPTER_ID,
                0L,
                "W/\"" + externalId + "\"",
                now,
                now
        );
    }
}
