package com.identra.identity.cache;

import com.identra.canonical.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "identra.cache.redis-enabled", havingValue = "true")
public class RedisIdentityCache implements IdentityCache {

    private final RedisTemplate<String, Identity> redisTemplate;
    private final Duration ttl;

    public RedisIdentityCache(
            RedisTemplate<String, Identity> redisTemplate,
            @Value("${identra.cache.identity-ttl-seconds:60}") long ttlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Override
    public Optional<Identity> get(UUID tenantId, UUID id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(tenantId, id)));
    }

    @Override
    public void put(Identity identity) {
        redisTemplate.opsForValue().set(key(identity.tenantId(), identity.id()), identity, ttl);
    }

    @Override
    public void evict(UUID tenantId, UUID id) {
        redisTemplate.delete(key(tenantId, id));
    }

    private static String key(UUID tenantId, UUID id) {
        return "identra:identity:" + tenantId + ":" + id;
    }
}
