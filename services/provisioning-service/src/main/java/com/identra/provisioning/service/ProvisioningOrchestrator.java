package com.identra.provisioning.service;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import com.identra.provisioning.config.TemporalConfig;
import com.identra.provisioning.temporal.ProvisioningWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProvisioningOrchestrator {

    private final ProvisioningJobStore jobStore;
    private final ObjectProvider<WorkflowClient> workflowClient;
    private final boolean temporalEnabled;

    public ProvisioningOrchestrator(
            ProvisioningJobStore jobStore,
            ObjectProvider<WorkflowClient> workflowClient,
            @Value("${identra.temporal.enabled:false}") boolean temporalEnabled
    ) {
        this.jobStore = jobStore;
        this.workflowClient = workflowClient;
        this.temporalEnabled = temporalEnabled;
    }

    public ProvisioningJob submit(
            UUID tenantId,
            String idempotencyKey,
            String correlationId,
            ProvisioningRequest request
    ) {
        return jobStore.findByIdempotencyKey(tenantId, idempotencyKey).orElseGet(() -> {
            ProvisioningJob job = jobStore.create(tenantId, idempotencyKey, correlationId, request);
            if (temporalEnabled) {
                WorkflowClient client = workflowClient.getIfAvailable();
                if (client != null) {
                    ProvisioningWorkflow workflow = client.newWorkflowStub(
                            ProvisioningWorkflow.class,
                            WorkflowOptions.newBuilder()
                                    .setTaskQueue(TemporalConfig.TASK_QUEUE)
                                    .setWorkflowId("provision-" + job.id())
                                    .build()
                    );
                    WorkflowClient.start(
                            workflow::run,
                            new ProvisioningWorkflow.ProvisioningWorkflowInput(
                                    job.id(),
                                    tenantId,
                                    job.correlationId(),
                                    request
                            )
                    );
                    jobStore.updateStatus(job.id(), "RUNNING", "Temporal workflow started");
                    return jobStore.get(tenantId, job.id()).orElse(job);
                }
            }
            // Local fallback without Temporal: mark completed via stub activity path
            jobStore.updateStatus(job.id(), "SUCCEEDED", "Completed via local stub (temporal disabled)");
            return jobStore.get(tenantId, job.id()).orElse(job);
        });
    }

    public ProvisioningJob get(UUID tenantId, UUID jobId) {
        return jobStore.get(tenantId, jobId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Job not found"));
    }
}
