package com.identra.identity.cache;

import com.identra.canonical.Identity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnMissingBean(IdentityCache.class)
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
