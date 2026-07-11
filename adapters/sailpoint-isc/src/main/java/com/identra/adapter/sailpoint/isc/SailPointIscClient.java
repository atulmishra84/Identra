package com.identra.adapter.sailpoint.isc;

import com.identra.canonical.Identity;
import com.identra.canonical.MappingSet;
import com.identra.translation.AttributeTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** SailPoint ISC Identities API client (v3). */
public class SailPointIscClient {

    private static final Logger log = LoggerFactory.getLogger(SailPointIscClient.class);

    private final SailPointIscClientConfig config;
    private final RestClient restClient;
    private final MappingSet mapping = MappingSet.identityPassthrough("sailpoint-isc");

    public SailPointIscClient(SailPointIscClientConfig config) {
        this.config = config;
        this.restClient = RestClient.builder()
                .baseUrl(trimSlash(config.baseUrl()))
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<Map<String, Object>> getIdentity(String externalId) {
        if (config.dryRun()) {
            return Optional.of(dryIdentity(externalId));
        }
        Map<String, Object> body = restClient.get()
                .uri("/v3/identities/{id}", externalId)
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return Optional.ofNullable(body);
    }

    public Map<String, Object> createIdentity(Identity identity) {
        Map<String, Object> attrs = AttributeTranslator.toVendor(identity, mapping);
        if (config.dryRun()) {
            String id = "isc-dry-" + UUID.randomUUID();
            log.info("ISC dry-run create userName={}", identity.userName());
            return dryIdentity(id, identity.userName());
        }
        return restClient.post()
                .uri("/v3/identities")
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "name", identity.userName(),
                        "attributes", attrs
                ))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> updateIdentity(String externalId, Identity identity) {
        Map<String, Object> attrs = AttributeTranslator.toVendor(identity, mapping);
        if (config.dryRun()) {
            log.info("ISC dry-run update id={}", externalId);
            return dryIdentity(externalId, identity.userName());
        }
        return restClient.patch()
                .uri("/v3/identities/{id}", externalId)
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("attributes", attrs))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void disable(String externalId) {
        if (config.dryRun()) {
            log.info("ISC dry-run disable id={}", externalId);
            return;
        }
        // ISC disable is typically via account lifecycle; MVP uses identity attribute patch.
        restClient.patch()
                .uri("/v3/identities/{id}", externalId)
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("attributes", Map.of("cloudLifecycleState", "inactive")))
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(String externalId) {
        if (config.dryRun()) {
            log.info("ISC dry-run delete id={}", externalId);
            return;
        }
        restClient.delete()
                .uri("/v3/identities/{id}", externalId)
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .retrieve()
                .toBodilessEntity();
    }

    public List<Map<String, Object>> search(String filter, int limit) {
        if (config.dryRun()) {
            return List.of(dryIdentity("isc-dry-1", "dryrun.user"));
        }
        Map<String, Object> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v3/identities")
                        .queryParam("filters", filter == null ? "" : filter)
                        .queryParam("limit", Math.max(1, Math.min(limit, 250)))
                        .build())
                .header("Authorization", "Bearer " + clientCredentialsToken())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return List.of();
        }
        Object list = response.get("items");
        if (list instanceof List<?> items) {
            return items.stream()
                    .filter(Map.class::isInstance)
                    .map(i -> castMap((Map<?, ?>) i))
                    .toList();
        }
        return List.of();
    }

    public Identity toCanonical(Map<String, Object> iscIdentity, UUID tenantId) {
        String id = string(iscIdentity.get("id"));
        String name = string(iscIdentity.get("name"));
        Object attrsObj = iscIdentity.get("attributes");
        Map<String, Object> attrs = attrsObj instanceof Map<?, ?> m ? castMap(m) : Map.of();
        Instant now = Instant.now();
        return new Identity(
                UUID.nameUUIDFromBytes(("isc:" + id).getBytes()),
                tenantId,
                List.of(new Identity.ExternalId("sailpoint-isc", id)),
                name == null ? id : name,
                AttributeTranslator.emailsFromVendor(attrs),
                AttributeTranslator.nameFromVendor(attrs, mapping),
                !"inactive".equalsIgnoreCase(string(attrs.get("cloudLifecycleState"))),
                string(attrs.get("department")),
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of("iscName", name == null ? "" : name),
                "sailpoint-isc",
                0L,
                "W/\"" + id + "\"",
                now,
                now
        );
    }

    private String clientCredentialsToken() {
        // MVP: expect pre-supplied token in clientSecret field when not using dry-run.
        // Full OAuth client-credentials exchange can replace this in hardening.
        return config.clientSecret() == null ? "" : config.clientSecret();
    }

    private static Map<String, Object> dryIdentity(String id) {
        return dryIdentity(id, "dryrun.user");
    }

    private static Map<String, Object> dryIdentity(String id, String name) {
        Map<String, Object> attrs = new LinkedHashMap<>();
        attrs.put("email", name.contains("@") ? name : name + "@example.com");
        attrs.put("firstName", "Dry");
        attrs.put("lastName", "Run");
        attrs.put("department", "Engineering");
        attrs.put("cloudLifecycleState", "active");
        Map<String, Object> identity = new LinkedHashMap<>();
        identity.put("id", id);
        identity.put("name", name);
        identity.put("attributes", attrs);
        return identity;
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

    private static Map<String, Object> castMap(Map<?, ?> map) {
        Map<String, Object> out = new LinkedHashMap<>();
        map.forEach((k, v) -> out.put(String.valueOf(k), v));
        return out;
    }
}
