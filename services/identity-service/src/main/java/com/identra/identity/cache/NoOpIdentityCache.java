package com.identra.identity.cache;

import com.identra.canonical.Identity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "identra.cache.redis-enabled", havingValue = "false", matchIfMissing = true)
public class NoOpIdentityCache implements IdentityCache {

    @Override
    public Optional<Identity> get(UUID tenantId, UUID id) {
        return Optional.empty();
    }

    @Override
    public void put(Identity identity) {
        // no-op
    }

    @Override
    public void evict(UUID tenantId, UUID id) {
        // no-op
    }
}
