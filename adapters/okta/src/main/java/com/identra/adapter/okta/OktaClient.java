package com.identra.adapter.okta;

import com.identra.canonical.Identity;
import com.identra.canonical.MappingSet;
import com.identra.translation.AttributeTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Thin Okta Users API client (/api/v1/users).
 */
public class OktaClient {

    private static final Logger log = LoggerFactory.getLogger(OktaClient.class);

    private final OktaClientConfig config;
    private final RestClient restClient;
    private final MappingSet mapping;

    public OktaClient(OktaClientConfig config) {
        this.config = config;
        this.mapping = MappingSet.identityPassthrough("okta");
        this.restClient = RestClient.builder()
                .baseUrl(trimSlash(config.orgUrl()))
                .defaultHeader("Authorization", "SSWS " + (config.apiToken() == null ? "" : config.apiToken()))
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<Map<String, Object>> getUser(String externalId) {
        if (config.dryRun()) {
            return Optional.of(dryUser(externalId, "dryrun.user@example.com"));
        }
        Map<String, Object> body = restClient.get()
                .uri("/api/v1/users/{id}", externalId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return Optional.ofNullable(body);
    }

    public Map<String, Object> createUser(Identity identity) {
        Map<String, Object> profile = AttributeTranslator.toVendor(identity, mapping);
        Map<String, Object> payload = Map.of("profile", profile);
        if (config.dryRun()) {
            String id = "okta-dry-" + UUID.randomUUID();
            log.info("Okta dry-run create userName={}", identity.userName());
            return dryUser(id, firstEmail(identity));
        }
        return restClient.post()
                .uri("/api/v1/users?activate=true")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> updateUser(String externalId, Identity identity) {
        Map<String, Object> profile = AttributeTranslator.toVendor(identity, mapping);
        if (config.dryRun()) {
            log.info("Okta dry-run update id={}", externalId);
            return dryUser(externalId, firstEmail(identity));
        }
        return restClient.post()
                .uri("/api/v1/users/{id}", externalId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("profile", profile))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void deactivate(String externalId) {
        if (config.dryRun()) {
            log.info("Okta dry-run deactivate id={}", externalId);
            return;
        }
        restClient.post()
                .uri("/api/v1/users/{id}/lifecycle/deactivate", externalId)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(String externalId) {
        if (config.dryRun()) {
            log.info("Okta dry-run delete id={}", externalId);
            return;
        }
        restClient.delete()
                .uri("/api/v1/users/{id}", externalId)
                .retrieve()
                .toBodilessEntity();
    }

    public List<Map<String, Object>> search(String filter, int limit) {
        if (config.dryRun()) {
            return List.of(dryUser("okta-dry-1", "dryrun.user@example.com"));
        }
        List<Map<String, Object>> users = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users")
                        .queryParam("search", filter == null ? "" : filter)
                        .queryParam("limit", Math.max(1, Math.min(limit, 200)))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return users == null ? List.of() : users;
    }

    public Identity toCanonical(Map<String, Object> oktaUser, UUID tenantId) {
        String id = string(oktaUser.get("id"));
        Object profileObj = oktaUser.get("profile");
        Map<String, Object> profile = profileObj instanceof Map<?, ?> m
                ? castMap(m)
                : Map.of();
        String login = string(profile.get("login"));
        Instant now = Instant.now();
        return new Identity(
                UUID.nameUUIDFromBytes(("okta:" + id).getBytes()),
                tenantId,
                List.of(new Identity.ExternalId("okta", id)),
                login == null ? id : login,
                AttributeTranslator.emailsFromVendor(profile),
                AttributeTranslator.nameFromVendor(profile, mapping),
                !"DEPROVISIONED".equalsIgnoreCase(string(oktaUser.get("status"))),
                string(profile.get("department")),
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of("oktaStatus", string(oktaUser.get("status"))),
                "okta",
                0L,
                "W/\"" + id + "\"",
                now,
                now
        );
    }

    private static Map<String, Object> dryUser(String id, String email) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("login", email);
        profile.put("email", email);
        profile.put("firstName", "Dry");
        profile.put("lastName", "Run");
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", id);
        user.put("status", "ACTIVE");
        user.put("profile", profile);
        return user;
    }

    private static String firstEmail(Identity identity) {
        if (identity.emails() == null || identity.emails().isEmpty()) {
            return identity.userName();
        }
        return identity.emails().getFirst().value();
    }

    private static String trimSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> map) {
        Map<String, Object> out = new LinkedHashMap<>();
        map.forEach((k, v) -> out.put(String.valueOf(k), v));
        return out;
    }
}
