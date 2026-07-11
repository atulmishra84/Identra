package com.identra.provisioning.temporal;

import com.identra.canonical.ProvisioningRequest;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.UUID;

public class ProvisioningWorkflowImpl implements ProvisioningWorkflow {

    private final ProvisioningActivities activities = Workflow.newActivityStub(
            ProvisioningActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(2))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(3)
                            .build())
                    .build()
    );

    @Override
    public ProvisioningWorkflowResult run(ProvisioningWorkflowInput input) {
        UUID jobId = input.jobId();
        try {
            String detail = activities.executeAdapterOperation(input.tenantId(), input.request());
            activities.markJobCompleted(jobId, "SUCCEEDED", detail);
            return new ProvisioningWorkflowResult(jobId, "SUCCEEDED", detail);
        } catch (Exception ex) {
            String message = ex.getMessage() == null ? "Provisioning failed" : ex.getMessage();
            activities.markJobCompleted(jobId, "FAILED", message);
            return new ProvisioningWorkflowResult(jobId, "FAILED", message);
        }
    }
}
