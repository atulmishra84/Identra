package com.identra.tenant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants")
public class TenantController {

    @GetMapping("/{tenantId}")
    public Map<String, Object> get(@PathVariable UUID tenantId) {
        return Map.of(
                "id", tenantId,
                "name", "demo",
                "plan", "mvp",
                "deploymentProfile", "saas-shared",
                "status", "ACTIVE"
        );
    }
}
