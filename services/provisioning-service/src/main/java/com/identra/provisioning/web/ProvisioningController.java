package com.identra.provisioning.web;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import com.identra.provisioning.service.ProvisioningOrchestrator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/provisioning/jobs")
public class ProvisioningController {

    private final ProvisioningOrchestrator orchestrator;

    public ProvisioningController(ProvisioningOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ProvisioningJob> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestBody ProvisioningRequest request
    ) {
        ProvisioningJob job = orchestrator.submit(tenantId, idempotencyKey, correlationId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/{jobId}")
    public ProvisioningJob get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID jobId
    ) {
        return orchestrator.get(tenantId, jobId);
    }
}
