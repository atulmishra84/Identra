package com.identra.adapter.entra;

import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;
import com.identra.canonical.MappingSet;
import com.identra.translation.AttributeTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Microsoft Entra ID adapter (v2) — Graph users API shape, dry-run by default. */
public class EntraIdentityAdapter implements IdentityAdapter, HealthCapable {

    public static final String ADAPTER_ID = "entra";
    private static final Logger log = LoggerFactory.getLogger(EntraIdentityAdapter.class);

    private final boolean dryRun;
    private final UUID defaultTenantId;
    private final MappingSet mapping = MappingSet.identityPassthrough(ADAPTER_ID);

    public EntraIdentityAdapter() {
        this(true, UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    public EntraIdentityAdapter(boolean dryRun, UUID defaultTenantId) {
        this.dryRun = dryRun;
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    public CapabilitiesDescriptor capabilities() {
        return new CapabilitiesDescriptor(
                ADAPTER_ID,
                "Microsoft Entra ID",
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
        if (!dryRun) {
            throw new UnsupportedOperationException("Live Graph calls require IDENTRA_ENTRA_DRY_RUN=false + token wiring");
        }
        return Optional.of(canonical(externalId, "entra.user@example.com"));
    }

    @Override
    public Identity create(Identity identity) {
        Map<String, Object> vendor = AttributeTranslator.toVendor(identity, mapping);
        log.info("Entra dry-run create {}", vendor.get("userName"));
        return canonical("entra-dry-" + UUID.randomUUID(), identity.userName());
    }

    @Override
    public Identity update(Identity identity) {
        log.info("Entra dry-run update {}", identity.userName());
        return identity;
    }

    @Override
    public void disable(String externalId) {
        log.info("Entra dry-run disable {}", externalId);
    }

    @Override
    public void delete(String externalId) {
        log.info("Entra dry-run delete {}", externalId);
    }

    @Override
    public List<Identity> search(String filter, int startIndex, int count) {
        return List.of(canonical("entra-dry-1", "entra.user@example.com"));
    }

    @Override
    public HealthStatus health() {
        return HealthStatus.UP;
    }

    private Identity canonical(String externalId, String userName) {
        Instant now = Instant.now();
        return new Identity(
                UUID.nameUUIDFromBytes(("entra:" + externalId).getBytes()),
                defaultTenantId,
                List.of(new Identity.ExternalId(ADAPTER_ID, externalId)),
                userName,
                List.of(new Identity.Email(userName.contains("@") ? userName : userName + "@example.com", "work", true)),
                new Identity.Name("Entra User", "User", "Entra"),
                true,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of("graphResource", "/users"),
                ADAPTER_ID,
                0L,
                "W/\"" + externalId + "\"",
                now,
                now
        );
    }
}
