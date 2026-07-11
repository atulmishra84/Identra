package com.identra.audit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/audit/events")
public class AuditController {

    @GetMapping
    public List<Map<String, Object>> list(@RequestHeader("X-Tenant-Id") UUID tenantId) {
        return List.of(Map.of(
                "tenantId", tenantId,
                "eventType", "platform.bootstrap",
                "message", "Identra audit service online",
                "occurredAt", Instant.now().toString()
        ));
    }
}
