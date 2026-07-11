package com.identra.adapter.ping;

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

/** Ping Identity adapter (v3) — dry-run surface for catalog completeness. */
public class PingIdentityAdapter implements IdentityAdapter, HealthCapable {

    public static final String ADAPTER_ID = "ping";
    private static final Logger log = LoggerFactory.getLogger(PingIdentityAdapter.class);
    private final UUID defaultTenantId;

    public PingIdentityAdapter() {
        this(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    public PingIdentityAdapter(UUID defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                ADAPTER_ID,
                "Ping Identity",
                "0.1.0-SNAPSHOT",
                EnumSet.of(
                        CapabilitiesDescriptor.Capability.IDENTITY_CRUD,
                        CapabilitiesDescriptor.Capability.SEARCH,
                        CapabilitiesDescriptor.Capability.DISABLE,
                        CapabilitiesDescriptor.Capability.DELETE,
                        CapabilitiesDescriptor.Capability.DELTA_SYNC
                )
        );
    }

    @Override
    public Optional<Identity> getByExternalId(String externalId) {
        return Optional.of(canonical(externalId, ADAPTER_ID + ".user"));
    }

    @Override
    public Identity create(Identity identity) {
        log.info("Ping Identity dry-run create {}", identity.userName());
        return canonical(ADAPTER_ID + "-dry-" + UUID.randomUUID(), identity.userName());
    }

    @Override
    public Identity update(Identity identity) {
        log.info("Ping Identity dry-run update {}", identity.userName());
        return identity;
    }

    @Override
    public void disable(String externalId) {
        log.info("Ping Identity dry-run disable {}", externalId);
    }

    @Override
    public void delete(String externalId) {
        log.info("Ping Identity dry-run delete {}", externalId);
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        return List.of(canonical(ADAPTER_ID + "-dry-1", ADAPTER_ID + ".user"));
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UP;
    }

    private Identity canonical(String externalId, String userName) {
        Instant now = Instant.now();
        return new Identity(
                UUID.nameUUIDFromBytes((ADAPTER_ID + ":" + externalId).getBytes()),
                defaultTenantId,
                List.of(new Identity.ExternalId(ADAPTER_ID, externalId)),
                userName,
                List.of(),
                null,
                true,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of("vendor", "Ping Identity"),
                ADAPTER_ID,
                0L,
                "W/\"" + externalId + "\"",
                now,
                now
        );
    }
}
