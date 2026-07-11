package com.identra.tenant.persistence;

import com.identra.tenant.model.Tenant;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantRepository {

    private final JdbcTemplate jdbc;
    private final RowMapper<Tenant> mapper = (rs, rowNum) -> new Tenant(
            (UUID) rs.getObject("id"),
            rs.getString("name"),
            rs.getString("plan"),
            rs.getString("deployment_profile"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    public TenantRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Tenant> findById(UUID id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT * FROM tenant WHERE id = ?",
                    mapper,
                    id
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Tenant> list() {
        return jdbc.query("SELECT * FROM tenant ORDER BY name", mapper);
    }

    public Tenant insert(Tenant tenant) {
        jdbc.update(
                """
                INSERT INTO tenant (id, name, plan, deployment_profile, status, created_at, updated_at)
                VALUES (?,?,?,?,?,?,?)
                """,
                tenant.id(),
                tenant.name(),
                tenant.plan(),
                tenant.deploymentProfile(),
                tenant.status(),
                Timestamp.from(tenant.createdAt()),
                Timestamp.from(tenant.updatedAt())
        );
        return tenant;
    }

    public Optional<Tenant> update(Tenant tenant) {
        int updated = jdbc.update(
                """
                UPDATE tenant SET name = ?, plan = ?, deployment_profile = ?, status = ?, updated_at = ?
                WHERE id = ?
                """,
                tenant.name(),
                tenant.plan(),
                tenant.deploymentProfile(),
                tenant.status(),
                Timestamp.from(tenant.updatedAt()),
                tenant.id()
        );
        return updated == 0 ? Optional.empty() : Optional.of(tenant);
    }
}
