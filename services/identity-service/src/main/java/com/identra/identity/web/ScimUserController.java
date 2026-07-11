package com.identra.identity.web;

import com.identra.canonical.Identity;
import com.identra.canonical.IdentityWriteRequest;
import com.identra.identity.service.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SCIM 2.0 Users subset — applications should prefer this or /v1/identities over vendor APIs.
 */
@RestController
@RequestMapping(path = "/scim/v2/Users", produces = "application/scim+json")
public class ScimUserController {

    private final IdentityService identityService;

    public ScimUserController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "100") int count
    ) {
        List<Identity> identities;
        if (filter != null && filter.startsWith("userName eq ")) {
            String userName = stripQuotes(filter.substring("userName eq ".length()).trim());
            identities = identityService.list(tenantId, 1, 500).stream()
                    .filter(i -> i.userName().equalsIgnoreCase(userName))
                    .toList();
        } else {
            identities = identityService.list(tenantId, startIndex, count);
        }
        List<Map<String, Object>> resources = identities.stream().map(this::toScim).toList();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("schemas", List.of("urn:ietf:params:scim:api:messages:2.0:ListResponse"));
        response.put("totalResults", filter == null ? identityService.count(tenantId) : resources.size());
        response.put("startIndex", startIndex);
        response.put("itemsPerPage", resources.size());
        response.put("Resources", resources);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        return toScim(identityService.get(tenantId, id));
    }

    @PostMapping(consumes = {"application/scim+json", MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, Object> body
    ) {
        IdentityWriteRequest request = fromScim(body);
        Identity created = identityService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toScim(created));
    }

    private Map<String, Object> toScim(Identity identity) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("schemas", List.of("urn:ietf:params:scim:schemas:core:2.0:User"));
        user.put("id", identity.id().toString());
        user.put("userName", identity.userName());
        user.put("active", identity.active());
        if (identity.name() != null) {
            user.put("name", Map.of(
                    "formatted", nullToEmpty(identity.name().formatted()),
                    "familyName", nullToEmpty(identity.name().familyName()),
                    "givenName", nullToEmpty(identity.name().givenName())
            ));
        }
        if (identity.emails() != null) {
            user.put("emails", identity.emails());
        }
        user.put("meta", Map.of(
                "resourceType", "User",
                "created", identity.createdAt().toString(),
                "lastModified", identity.updatedAt().toString(),
                "version", identity.etag()
        ));
        return user;
    }

    @SuppressWarnings("unchecked")
    private IdentityWriteRequest fromScim(Map<String, Object> body) {
        String userName = String.valueOf(body.get("userName"));
        Boolean active = body.get("active") instanceof Boolean b ? b : Boolean.TRUE;
        Identity.Name name = null;
        if (body.get("name") instanceof Map<?, ?> nameMap) {
            name = new Identity.Name(
                    stringOrNull(nameMap.get("formatted")),
                    stringOrNull(nameMap.get("familyName")),
                    stringOrNull(nameMap.get("givenName"))
            );
        }
        List<Identity.Email> emails = List.of();
        if (body.get("emails") instanceof List<?> emailList) {
            emails = emailList.stream()
                    .filter(Map.class::isInstance)
                    .map(e -> (Map<String, Object>) e)
                    .map(e -> new Identity.Email(
                            stringOrNull(e.get("value")),
                            stringOrNull(e.get("type")),
                            Boolean.TRUE.equals(e.get("primary"))
                    ))
                    .toList();
        }
        return new IdentityWriteRequest(
                userName,
                emails,
                name,
                active,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                List.of(),
                "scim",
                Map.of()
        );
    }

    private static String stripQuotes(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String stringOrNull(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
