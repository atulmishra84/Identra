package com.identra.identity.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identra.canonical.Identity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IdentityRepository {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;
    private final RowMapper<Identity> rowMapper = this::mapRow;

    public IdentityRepository(JdbcTemplate jdbc, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    public Optional<Identity> findById(UUID tenantId, UUID id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    """
                    SELECT * FROM identity_record
                    WHERE tenant_id = ? AND id = ?
                    """,
                    rowMapper,
                    tenantId,
                    id
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Identity> findByUserName(UUID tenantId, String userName) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    """
                    SELECT * FROM identity_record
                    WHERE tenant_id = ? AND user_name = ?
                    """,
                    rowMapper,
                    tenantId,
                    userName
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Identity> list(UUID tenantId, int limit, int offset) {
        return jdbc.query(
                """
                SELECT * FROM identity_record
                WHERE tenant_id = ?
                ORDER BY user_name
                LIMIT ? OFFSET ?
                """,
                rowMapper,
                tenantId,
                limit,
                offset
        );
    }

    public long count(UUID tenantId) {
        Long count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM identity_record WHERE tenant_id = ?",
                Long.class,
                tenantId
        );
        return count == null ? 0L : count;
    }

    public Identity insert(Identity identity) {
        jdbc.update(
                """
                INSERT INTO identity_record (
                  id, tenant_id, user_name, active, department, manager_id, employment_status,
                  source_system, emails_json, name_json, external_ids_json, custom_attributes_json,
                  version, etag, created_at, updated_at
                ) VALUES (?,?,?,?,?,?,?,?,?::jsonb,?::jsonb,?::jsonb,?::jsonb,?,?,?,?)
                """,
                identity.id(),
                identity.tenantId(),
                identity.userName(),
                identity.active(),
                identity.department(),
                identity.managerId(),
                identity.employmentStatus() == null ? null : identity.employmentStatus().name(),
                identity.sourceSystem(),
                toJson(identity.emails() == null ? List.of() : identity.emails()),
                toJson(identity.name()),
                toJson(identity.externalIds() == null ? List.of() : identity.externalIds()),
                toJson(identity.customAttributes() == null ? Map.of() : identity.customAttributes()),
                identity.version(),
                identity.etag(),
                Timestamp.from(identity.createdAt()),
                Timestamp.from(identity.updatedAt())
        );
        return identity;
    }

    public Optional<Identity> update(Identity identity) {
        int updated = jdbc.update(
                """
                UPDATE identity_record SET
                  user_name = ?, active = ?, department = ?, manager_id = ?, employment_status = ?,
                  source_system = ?, emails_json = ?::jsonb, name_json = ?::jsonb,
                  external_ids_json = ?::jsonb, custom_attributes_json = ?::jsonb,
                  version = ?, etag = ?, updated_at = ?
                WHERE tenant_id = ? AND id = ? AND version = ?
                """,
                identity.userName(),
                identity.active(),
                identity.department(),
                identity.managerId(),
                identity.employmentStatus() == null ? null : identity.employmentStatus().name(),
                identity.sourceSystem(),
                toJson(identity.emails() == null ? List.of() : identity.emails()),
                toJson(identity.name()),
                toJson(identity.externalIds() == null ? List.of() : identity.externalIds()),
                toJson(identity.customAttributes() == null ? Map.of() : identity.customAttributes()),
                identity.version(),
                identity.etag(),
                Timestamp.from(identity.updatedAt()),
                identity.tenantId(),
                identity.id(),
                identity.version() - 1
        );
        return updated == 0 ? Optional.empty() : Optional.of(identity);
    }

    public boolean delete(UUID tenantId, UUID id) {
        return jdbc.update(
                "DELETE FROM identity_record WHERE tenant_id = ? AND id = ?",
                tenantId,
                id
        ) > 0;
    }

    private Identity mapRow(ResultSet rs, int rowNum) throws SQLException {
        String employment = rs.getString("employment_status");
        return new Identity(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("tenant_id"),
                readList(rs.getString("external_ids_json"), new TypeReference<>() {
                }),
                rs.getString("user_name"),
                readList(rs.getString("emails_json"), new TypeReference<>() {
                }),
                readValue(rs.getString("name_json"), Identity.Name.class),
                rs.getBoolean("active"),
                rs.getString("department"),
                (UUID) rs.getObject("manager_id"),
                employment == null ? null : Identity.EmploymentStatus.valueOf(employment),
                readMap(rs.getString("custom_attributes_json")),
                rs.getString("source_system"),
                rs.getLong("version"),
                rs.getString("etag"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize JSON column", e);
        }
    }

    private <T> T readValue(String json, Class<T> type) {
        if (json == null || json.isBlank() || "null".equals(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize JSON column", e);
        }
    }

    private <T> List<T> readList(String json, TypeReference<List<T>> type) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize JSON list column", e);
        }
    }

    private Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize JSON map column", e);
        }
    }
}
