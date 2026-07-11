package com.identra.provisioning;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accepts provisioning requests asynchronously (202). Temporal wiring lands in Sprints 4–6.
 */
@RestController
@RequestMapping("/v1/provisioning/jobs")
public class ProvisioningController {

    private final Map<UUID, ProvisioningJob> jobs = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<ProvisioningJob> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestBody ProvisioningRequest request
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
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ProvisioningJob> get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID jobId
    ) {
        ProvisioningJob job = jobs.get(jobId);
        if (job == null || !tenantId.equals(job.tenantId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }
}
