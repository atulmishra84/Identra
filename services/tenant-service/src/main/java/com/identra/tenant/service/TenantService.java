package com.identra.tenant.service;

import com.identra.tenant.model.Tenant;
import com.identra.tenant.persistence.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository repository;

    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    public Tenant get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }

    public List<Tenant> list() {
        return repository.list();
    }

    public Tenant create(String name, String plan, String deploymentProfile) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        Instant now = Instant.now();
        return repository.insert(new Tenant(
                UUID.randomUUID(),
                name.trim(),
                plan == null || plan.isBlank() ? "mvp" : plan,
                deploymentProfile == null || deploymentProfile.isBlank() ? "saas-shared" : deploymentProfile,
                "ACTIVE",
                now,
                now
        ));
    }

    public Tenant updateStatus(UUID id, String status) {
        if (status == null || !List.of("ACTIVE", "SUSPENDED", "DELETED").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be ACTIVE, SUSPENDED, or DELETED");
        }
        Tenant existing = get(id);
        return repository.update(new Tenant(
                existing.id(),
                existing.name(),
                existing.plan(),
                existing.deploymentProfile(),
                status,
                existing.createdAt(),
                Instant.now()
        )).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }
}
