package com.identra.provisioning.temporal;

import com.identra.canonical.Identity;
import com.identra.canonical.ProvisioningRequest;
import com.identra.provisioning.service.ProvisioningJobStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ProvisioningActivitiesImpl implements ProvisioningActivities {

    private final ProvisioningJobStore jobStore;
    private final RestClient restClient;
    private final String adapterRuntimeBase;
    private final String defaultAdapterId;

    public ProvisioningActivitiesImpl(
            ProvisioningJobStore jobStore,
            @Value("${identra.adapter-runtime-url:http://localhost:8085}") String adapterRuntimeBase,
            @Value("${identra.default-adapter-id:okta}") String defaultAdapterId
    ) {
        this.jobStore = jobStore;
        this.adapterRuntimeBase = adapterRuntimeBase.endsWith("/")
                ? adapterRuntimeBase.substring(0, adapterRuntimeBase.length() - 1)
                : adapterRuntimeBase;
        this.defaultAdapterId = defaultAdapterId;
        this.restClient = RestClient.create();
    }

    @Override
    public String executeAdapterOperation(UUID tenantId, ProvisioningRequest request) {
        String adapterId = request.applicationKey() == null || request.applicationKey().isBlank()
                ? defaultAdapterId
                : request.applicationKey();

        Instant now = Instant.now();
        String userName = "user";
        if (request.attributes() != null && request.attributes().get("userName") != null) {
            userName = String.valueOf(request.attributes().get("userName"));
        }
        String externalId = "";
        if (request.attributes() != null && request.attributes().get("externalId") != null) {
            externalId = String.valueOf(request.attributes().get("externalId"));
        }

        Identity identity = new Identity(
                request.identityId() == null ? UUID.randomUUID() : request.identityId(),
                tenantId,
                externalId.isBlank() ? List.of() : List.of(new Identity.ExternalId(adapterId, externalId)),
                userName,
                List.of(),
                null,
                true,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                request.attributes() == null ? Map.of() : request.attributes(),
                adapterId,
                0L,
                "W/\"provision\"",
                now,
                now
        );

        Map<String, Object> payload = Map.of(
                "tenantId", tenantId.toString(),
                "adapterId", adapterId,
                "operation", request.operation().name(),
                "identity", identity,
                "externalId", externalId
        );

        Map<String, Object> result = restClient.post()
                .uri(adapterRuntimeBase + "/v1/adapters/" + adapterId + "/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (result == null) {
            return "Adapter returned empty result";
        }
        return String.valueOf(result.getOrDefault("detail", result.get("status")));
    }

    @Override
    public void markJobCompleted(UUID jobId, String status, String detail) {
        jobStore.updateStatus(jobId, status, detail);
    }
}
