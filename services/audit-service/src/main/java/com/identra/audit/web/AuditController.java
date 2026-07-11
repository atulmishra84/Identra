package com.identra.audit.web;

import com.identra.audit.model.AuditEvent;
import com.identra.audit.service.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/audit/events")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditEvent> list(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return auditService.list(tenantId, limit);
    }

    @PostMapping
    public ResponseEntity<AuditEvent> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, Object> body
    ) {
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = body.get("payload") instanceof Map<?, ?> m
                ? (Map<String, Object>) m
                : Map.of();
        AuditEvent event = auditService.record(
                tenantId,
                String.valueOf(body.getOrDefault("eventType", "custom")),
                body.get("actor") == null ? null : String.valueOf(body.get("actor")),
                body.get("resourceType") == null ? null : String.valueOf(body.get("resourceType")),
                body.get("resourceId") == null ? null : String.valueOf(body.get("resourceId")),
                String.valueOf(body.getOrDefault("message", "")),
                payload
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
}
