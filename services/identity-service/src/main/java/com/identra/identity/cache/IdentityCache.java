package com.identra.identity.cache;

import com.identra.canonical.Identity;

import java.util.Optional;
import java.util.UUID;

public interface IdentityCache {

    Optional<Identity> get(UUID tenantId, UUID id);

    void put(Identity identity);

    void evict(UUID tenantId, UUID id);
}
