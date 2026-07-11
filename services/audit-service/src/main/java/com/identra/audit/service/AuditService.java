package com.identra.audit.service;

import com.identra.audit.model.AuditEvent;
import com.identra.audit.persistence.AuditEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository repository;
    private final boolean kafkaEnabled;

    public AuditService(
            AuditEventRepository repository,
            @Value("${identra.audit.kafka-enabled:false}") boolean kafkaEnabled
    ) {
        this.repository = repository;
        this.kafkaEnabled = kafkaEnabled;
    }

    public AuditEvent record(
            UUID tenantId,
            String eventType,
            String actor,
            String resourceType,
            String resourceId,
            String message,
            Map<String, Object> payload
    ) {
        AuditEvent event = new AuditEvent(
                UUID.randomUUID(),
                tenantId,
                eventType,
                actor,
                resourceType,
                resourceId,
                message,
                payload == null ? Map.of() : payload,
                Instant.now()
        );
        repository.insert(event);
        if (kafkaEnabled) {
            // Kafka publisher wired when IDENTRA_AUDIT_KAFKA_ENABLED=true (outbox pattern in hardening).
        }
        return event;
    }

    public List<AuditEvent> list(UUID tenantId, int limit) {
        return repository.list(tenantId, limit);
    }
}
