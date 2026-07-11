package com.identra.adapter.spi;

import com.identra.canonical.Identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Vendor-neutral identity adapter contract.
 * Prefer external-id methods for vendor systems; UUID methods are Fabric-side helpers.
 */
public interface IdentityAdapter {

    CapabilitiesDescriptor capabilities();

    default Optional<Identity> get(UUID identityId) {
        return Optional.empty();
    }

    Optional<Identity> getByExternalId(String externalId);

    Identity create(Identity identity);

    Identity update(Identity identity);

    void disable(String externalId);

    void delete(String externalId);

    default void disable(UUID identityId) {
        throw new UnsupportedOperationException("Use disable(externalId)");
    }

    default void delete(UUID identityId) {
        throw new UnsupportedOperationException("Use delete(externalId)");
    }

    List<Identity> search(String filter, int startIndex, int count);
}
