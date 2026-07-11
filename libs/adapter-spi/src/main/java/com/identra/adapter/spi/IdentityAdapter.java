package com.identra.adapter.spi;

import com.identra.canonical.Identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Vendor-neutral identity adapter contract. */
public interface IdentityAdapter {

    CapabilitiesDescriptor capabilities();

    Optional<Identity> get(UUID identityId);

    Identity create(Identity identity);

    Identity update(Identity identity);

    void disable(UUID identityId);

    void delete(UUID identityId);

    List<Identity> search(String filter, int startIndex, int count);
}
