package com.identra.provisioning.service;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProvisioningJobStore {

    private final Map<UUID, ProvisioningJob> jobs = new ConcurrentHashMap<>();
    private final Map<String, UUID> idempotencyIndex = new ConcurrentHashMap<>();

    public Optional<ProvisioningJob> findByIdempotencyKey(UUID tenantId, String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        UUID jobId = idempotencyIndex.get(tenantId + ":" + key);
        if (jobId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jobs.get(jobId)).filter(j -> tenantId.equals(j.tenantId()));
    }

    public ProvisioningJob create(
            UUID tenantId,
            String idempotencyKey,
            String correlationId,
            ProvisioningRequest request
    ) {
        UUID jobId = UUID.randomUUID();
        Instant now = Instant.now();
        ProvisioningJob job = new ProvisioningJob(
                jobId,
                tenantId,
                "ACCEPTED",
                request.operation(),
                request.identityId(),
                request.applicationKey(),
                correlationId != null ? correlationId : jobId.toString(),
                null,
                now,
                now,
                null
        );
        jobs.put(jobId, job);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyIndex.put(tenantId + ":" + idempotencyKey, jobId);
        }
        return job;
    }

    public Optional<ProvisioningJob> get(UUID tenantId, UUID jobId) {
        return Optional.ofNullable(jobs.get(jobId)).filter(j -> tenantId.equals(j.tenantId()));
    }

    public void updateStatus(UUID jobId, String status, String detail) {
        ProvisioningJob existing = jobs.get(jobId);
        if (existing == null) {
            return;
        }
        Instant now = Instant.now();
        jobs.put(jobId, new ProvisioningJob(
                existing.id(),
                existing.tenantId(),
                status,
                existing.operation(),
                existing.identityId(),
                existing.applicationKey(),
                existing.correlationId(),
                detail,
                existing.createdAt(),
                now,
                "SUCCEEDED".equals(status) || "FAILED".equals(status) ? now : null
        ));
    }
}
