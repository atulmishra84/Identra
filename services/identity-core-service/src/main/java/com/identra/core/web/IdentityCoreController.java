package com.identra.core.web;

import com.identra.core.service.IdentityCoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/core")
public class IdentityCoreController {

    private final IdentityCoreService coreService;

    public IdentityCoreController(IdentityCoreService coreService) {
        this.coreService = coreService;
    }

    @PostMapping("/identities")
    public ResponseEntity<Map<String, Object>> createIdentity(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, Object> body
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coreService.createIdentity(tenantId, body));
    }

    @PutMapping("/identities/{id}/lifecycle/{state}")
    public Map<String, Object> lifecycle(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @PathVariable String state
    ) {
        return coreService.transitionLifecycle(tenantId, id, state);
    }

    @PostMapping("/roles")
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreService.createRole(tenantId, body.get("name"), body.get("description")));
    }

    @PostMapping("/access-requests")
    public ResponseEntity<Map<String, Object>> accessRequest(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coreService.createAccessRequest(
                tenantId,
                UUID.fromString(body.get("identityId")),
                UUID.fromString(body.get("roleId"))
        ));
    }

    @PostMapping("/access-requests/{id}/decision")
    public Map<String, Object> decide(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {
        return coreService.decideAccessRequest(tenantId, id, body.get("decision"));
    }
}
