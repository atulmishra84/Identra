package com.identra.provisioning.temporal;

import com.identra.canonical.ProvisioningJob;
import com.identra.canonical.ProvisioningRequest;
import com.identra.provisioning.service.ProvisioningJobStore;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProvisioningActivitiesImpl implements ProvisioningActivities {

    private final ProvisioningJobStore jobStore;

    public ProvisioningActivitiesImpl(ProvisioningJobStore jobStore) {
        this.jobStore = jobStore;
    }

    @Override
    public String executeAdapterOperation(UUID tenantId, ProvisioningRequest request) {
        // Adapter-runtime invocation lands fully in Sprints 4–6 (Okta first).
        return "Accepted operation " + request.operation()
                + " for application " + request.applicationKey()
                + " (adapter dispatch stub)";
    }

    @Override
    public void markJobCompleted(UUID jobId, String status, String detail) {
        jobStore.updateStatus(jobId, status, detail);
    }
}
