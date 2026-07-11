package com.identra.automation.runtime.web;

import com.identra.automation.runtime.service.AutomationExecutionService;
import com.identra.canonical.Identity;
import com.identra.canonical.ProvisioningRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/automation")
public class AutomationRuntimeController {

    private final AutomationExecutionService executionService;

    public AutomationRuntimeController(AutomationExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/packs/{packId}/execute")
    public Map<String, Object> execute(@PathVariable UUID packId, @RequestBody Map<String, Object> body) {
        ProvisioningRequest.Operation operation = ProvisioningRequest.Operation.valueOf(
                String.valueOf(body.getOrDefault("operation", "CREATE"))
        );
        Identity identity = null;
        if (body.get("identity") instanceof Map<?, ?> ignored) {
            // Identity is optional for dry-run execute; full bind uses typed DTO later.
            identity = null;
        }
        return executionService.execute(packId, operation, identity);
    }

    @GetMapping("/executions/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        Map<String, Object> execution = executionService.get(id);
        if (execution == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Execution not found");
        }
        return execution;
    }
}
