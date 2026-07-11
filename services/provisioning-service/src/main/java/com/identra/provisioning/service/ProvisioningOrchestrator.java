package com.identra.provisioning.service;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import com.identra.provisioning.config.TemporalConfig;
import com.identra.provisioning.temporal.ProvisioningActivities;
import com.identra.provisioning.temporal.ProvisioningWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class ProvisioningOrchestrator {

    private final ProvisioningJobStore jobStore;
    private final ObjectProvider<WorkflowClient> workflowClient;
    private final ObjectProvider<ProvisioningActivities> activities;
    private final boolean temporalEnabled;
    private final boolean approvalsRequired;
    private final String approvalServiceUrl;
    private final RestClient restClient = RestClient.create();

    public ProvisioningOrchestrator(
            ProvisioningJobStore jobStore,
            ObjectProvider<WorkflowClient> workflowClient,
            ObjectProvider<ProvisioningActivities> activities,
            @Value("${identra.temporal.enabled:false}") boolean temporalEnabled,
            @Value("${identra.approvals.required:false}") boolean approvalsRequired,
            @Value("${identra.approvals.service-url:http://localhost:8086}") String approvalServiceUrl
    ) {
        this.jobStore = jobStore;
        this.workflowClient = workflowClient;
        this.activities = activities;
        this.temporalEnabled = temporalEnabled;
        this.approvalsRequired = approvalsRequired;
        this.approvalServiceUrl = approvalServiceUrl.endsWith("/")
                ? approvalServiceUrl.substring(0, approvalServiceUrl.length() - 1)
                : approvalServiceUrl;
    }

    public ProvisioningJob submit(
            UUID tenantId,
            String idempotencyKey,
            String correlationId,
            ProvisioningRequest request
    ) {
        return jobStore.findByIdempotencyKey(tenantId, idempotencyKey).orElseGet(() -> {
            ProvisioningJob job = jobStore.create(tenantId, idempotencyKey, correlationId, request);

            if (approvalsRequired) {
                restClient.post()
                        .uri(approvalServiceUrl + "/v1/approvals")
                        .header("X-Tenant-Id", tenantId.toString())
                        .body(Map.of(
                                "jobId", job.id().toString(),
                                "title", "Approve " + request.operation() + " on " + request.applicationKey(),
                                "requestedBy", "provisioning-service"
                        ))
                        .retrieve()
                        .toBodilessEntity();
                jobStore.updateStatus(job.id(), "PENDING_APPROVAL", "Waiting for approval");
                return jobStore.get(tenantId, job.id()).orElse(job);
            }

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

            ProvisioningActivities activity = activities.getIfAvailable();
            if (activity != null) {
                String detail = activity.executeAdapterOperation(tenantId, request);
                activity.markJobCompleted(job.id(), "SUCCEEDED", detail);
            } else {
                jobStore.updateStatus(job.id(), "SUCCEEDED", "Completed via local stub");
            }
            return jobStore.get(tenantId, job.id()).orElse(job);
        });
    }

    public ProvisioningJob get(UUID tenantId, UUID jobId) {
        return jobStore.get(tenantId, jobId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Job not found"));
    }
}
