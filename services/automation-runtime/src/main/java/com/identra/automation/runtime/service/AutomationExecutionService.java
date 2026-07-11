package com.identra.automation.runtime.service;

import com.identra.canonical.Identity;
import com.identra.canonical.ProvisioningRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Executes automation packs in an isolated worker conceptual model.
 * Secrets are never accepted from prompts — only runtime env/vault refs.
 */
@Service
public class AutomationExecutionService {

    private static final Logger log = LoggerFactory.getLogger(AutomationExecutionService.class);

    private final boolean dryRun;
    private final Map<UUID, Map<String, Object>> executions = new ConcurrentHashMap<>();

    public AutomationExecutionService(@Value("${identra.automation.dry-run:true}") boolean dryRun) {
        this.dryRun = dryRun;
    }

    public Map<String, Object> execute(UUID packId, ProvisioningRequest.Operation operation, Identity identity) {
        UUID executionId = UUID.randomUUID();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("executionId", executionId);
        result.put("packId", packId);
        result.put("operation", operation.name());
        result.put("identityUserName", identity == null ? null : identity.userName());
        result.put("workerPool", "automation-workers");
        result.put("startedAt", Instant.now().toString());
        result.put("secretsSource", "vault://identra/automation/" + packId);
        if (dryRun) {
            log.info("Automation dry-run pack={} op={}", packId, operation);
            result.put("status", "SUCCEEDED");
            result.put("detail", "Dry-run automation completed without launching a browser/desktop session");
            result.put("recordingUri", null);
        } else {
            // Live mode would schedule onto isolated worker VMs/containers.
            result.put("status", "QUEUED");
            result.put("detail", "Queued for isolated automation worker");
        }
        result.put("completedAt", Instant.now().toString());
        executions.put(executionId, result);
        return result;
    }

    public Map<String, Object> get(UUID executionId) {
        return executions.get(executionId);
    }
}
