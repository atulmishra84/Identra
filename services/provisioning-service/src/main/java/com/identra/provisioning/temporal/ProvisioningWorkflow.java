package com.identra.provisioning.temporal;

import com.identra.canonical.ProvisioningRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface ProvisioningWorkflow {

    @WorkflowMethod
    ProvisioningWorkflowResult run(ProvisioningWorkflowInput input);

    record ProvisioningWorkflowInput(
            UUID jobId,
            UUID tenantId,
            String correlationId,
            ProvisioningRequest request
    ) {
    }

    record ProvisioningWorkflowResult(
            UUID jobId,
            String status,
            String detail
    ) {
    }
}
