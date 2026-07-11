package com.identra.core.service;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IdentityCoreService {

    private final JdbcTemplate jdbc;

    public IdentityCoreService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> createIdentity(UUID tenantId, Map<String, Object> body) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        String userName = required(body, "userName");
        jdbc.update(
                """
                INSERT INTO core_identity
                  (id, tenant_id, user_name, email, active, department, manager_id, lifecycle_state, created_at, updated_at)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """,
                id,
                tenantId,
                userName,
                body.get("email") == null ? null : String.valueOf(body.get("email")),
                body.get("active") instanceof Boolean b ? b : true,
                body.get("department") == null ? null : String.valueOf(body.get("department")),
                body.get("managerId") == null ? null : UUID.fromString(String.valueOf(body.get("managerId"))),
                "JOINER",
                Timestamp.from(now),
                Timestamp.from(now)
        );
        return getIdentity(tenantId, id);
    }

    public Map<String, Object> getIdentity(UUID tenantId, UUID id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM core_identity WHERE tenant_id = ? AND id = ?",
                tenantId,
                id
        );
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity not found");
        }
        return rows.getFirst();
    }

    public Map<String, Object> transitionLifecycle(UUID tenantId, UUID id, String state) {
        if (!List.of("JOINER", "ACTIVE", "MOVER", "LEAVER", "TERMINATED").contains(state)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid lifecycle state");
        }
        int updated = jdbc.update(
                "UPDATE core_identity SET lifecycle_state = ?, active = ?, updated_at = ? WHERE tenant_id = ? AND id = ?",
                state,
                !"LEAVER".equals(state) && !"TERMINATED".equals(state),
                Timestamp.from(Instant.now()),
                tenantId,
                id
        );
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity not found");
        }
        return getIdentity(tenantId, id);
    }

    public Map<String, Object> createRole(UUID tenantId, String name, String description) {
        UUID id = UUID.randomUUID();
        jdbc.update(
                "INSERT INTO core_role (id, tenant_id, name, description) VALUES (?,?,?,?)",
                id,
                tenantId,
                name,
                description
        );
        return Map.of("id", id, "tenantId", tenantId, "name", name, "description", description);
    }

    public void assignRole(UUID tenantId, UUID identityId, UUID roleId) {
        jdbc.update(
                "INSERT INTO core_role_assignment (tenant_id, identity_id, role_id) VALUES (?,?,?) ON CONFLICT DO NOTHING",
                tenantId,
                identityId,
                roleId
        );
    }

    public Map<String, Object> createAccessRequest(UUID tenantId, UUID identityId, UUID roleId) {
        UUID id = UUID.randomUUID();
        jdbc.update(
                """
                INSERT INTO core_access_request (id, tenant_id, identity_id, role_id, status, created_at)
                VALUES (?,?,?,?, 'PENDING', ?)
                """,
                id,
                tenantId,
                identityId,
                roleId,
                Timestamp.from(Instant.now())
        );
        return Map.of("id", id, "status", "PENDING", "identityId", identityId, "roleId", roleId);
    }

    public Map<String, Object> decideAccessRequest(UUID tenantId, UUID requestId, String decision) {
        if (!List.of("APPROVED", "DENIED").contains(decision)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decision must be APPROVED or DENIED");
        }
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM core_access_request WHERE tenant_id = ? AND id = ?",
                tenantId,
                requestId
        );
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access request not found");
        }
        Map<String, Object> request = rows.getFirst();
        jdbc.update(
                "UPDATE core_access_request SET status = ?, decided_at = ? WHERE id = ?",
                decision,
                Timestamp.from(Instant.now()),
                requestId
        );
        if ("APPROVED".equals(decision)) {
            assignRole(tenantId, (UUID) request.get("identity_id"), (UUID) request.get("role_id"));
        }
        return Map.of("id", requestId, "status", decision);
    }

    private static String required(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " is required");
        }
        return String.valueOf(value);
    }
}
