package com.identra.identity.service;

import com.identra.canonical.Identity;
import com.identra.canonical.IdentityWriteRequest;
import com.identra.identity.cache.IdentityCache;
import com.identra.identity.persistence.IdentityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IdentityService {

    private final IdentityRepository repository;
    private final IdentityCache cache;

    public IdentityService(IdentityRepository repository, IdentityCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public Identity get(UUID tenantId, UUID id) {
        return cache.get(tenantId, id).orElseGet(() -> {
            Identity identity = repository.findById(tenantId, id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity not found"));
            cache.put(identity);
            return identity;
        });
    }

    public List<Identity> list(UUID tenantId, int startIndex, int count) {
        int limit = Math.min(Math.max(count, 0), 200);
        int offset = Math.max(startIndex - 1, 0);
        return repository.list(tenantId, limit, offset);
    }

    public long count(UUID tenantId) {
        return repository.count(tenantId);
    }

    public Identity create(UUID tenantId, IdentityWriteRequest request) {
        repository.findByUserName(tenantId, request.userName()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "userName already exists");
        });
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();
        Identity identity = new Identity(
                id,
                tenantId,
                request.externalIds(),
                request.userName(),
                request.emails(),
                request.name(),
                Boolean.TRUE.equals(request.active()),
                request.department(),
                request.managerId(),
                request.employmentStatus() == null ? Identity.EmploymentStatus.ACTIVE : request.employmentStatus(),
                request.customAttributes(),
                request.sourceSystem() == null ? "identra" : request.sourceSystem(),
                0L,
                "W/\"" + id + "-0\"",
                now,
                now
        );
        Identity saved = repository.insert(identity);
        cache.put(saved);
        return saved;
    }

    public Identity replace(UUID tenantId, UUID id, IdentityWriteRequest request) {
        Identity existing = get(tenantId, id);
        Instant now = Instant.now();
        long nextVersion = existing.version() + 1;
        Identity updated = new Identity(
                existing.id(),
                tenantId,
                request.externalIds(),
                request.userName(),
                request.emails(),
                request.name(),
                Boolean.TRUE.equals(request.active()),
                request.department(),
                request.managerId(),
                request.employmentStatus() == null ? existing.employmentStatus() : request.employmentStatus(),
                request.customAttributes(),
                request.sourceSystem() == null ? existing.sourceSystem() : request.sourceSystem(),
                nextVersion,
                "W/\"" + existing.id() + "-" + nextVersion + "\"",
                existing.createdAt(),
                now
        );
        Identity saved = repository.update(updated)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Identity was modified concurrently"));
        cache.put(saved);
        return saved;
    }

    public void delete(UUID tenantId, UUID id) {
        if (!repository.delete(tenantId, id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity not found");
        }
        cache.evict(tenantId, id);
    }
}
