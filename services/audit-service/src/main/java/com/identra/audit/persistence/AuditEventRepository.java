package com.identra.audit.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identra.audit.model.AuditEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class AuditEventRepository {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;
    private final RowMapper<AuditEvent> mapper = (rs, rowNum) -> new AuditEvent(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("tenant_id"),
            rs.getString("event_type"),
            rs.getString("actor"),
            rs.getString("resource_type"),
            rs.getString("resource_id"),
            rs.getString("message"),
            readMap(rs.getString("payload_json")),
            rs.getTimestamp("occurred_at").toInstant()
    );

    public AuditEventRepository(JdbcTemplate jdbc, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    public void insert(AuditEvent event) {
        jdbc.update(
                """
                INSERT INTO audit_event
                  (id, tenant_id, event_type, actor, resource_type, resource_id, message, payload_json, occurred_at)
                VALUES (?,?,?,?,?,?,?,?::jsonb,?)
                """,
                event.id(),
                event.tenantId(),
                event.eventType(),
                event.actor(),
                event.resourceType(),
                event.resourceId(),
                event.message(),
                toJson(event.payload() == null ? Map.of() : event.payload()),
                Timestamp.from(event.occurredAt())
        );
    }

    public List<AuditEvent> list(UUID tenantId, int limit) {
        return jdbc.query(
                """
                SELECT * FROM audit_event
                WHERE tenant_id = ?
                ORDER BY occurred_at DESC
                LIMIT ?
                """,
                mapper,
                tenantId,
                Math.min(Math.max(limit, 1), 500)
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, Object> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
