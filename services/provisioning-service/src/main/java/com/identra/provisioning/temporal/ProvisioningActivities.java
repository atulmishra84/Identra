package com.identra.provisioning.temporal;

import com.identra.canonical.ProvisioningRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.UUID;

@ActivityInterface
public interface ProvisioningActivities {

    @ActivityMethod
    String executeAdapterOperation(UUID tenantId, ProvisioningRequest request);

    @ActivityMethod
    void markJobCompleted(UUID jobId, String status, String detail);
}
