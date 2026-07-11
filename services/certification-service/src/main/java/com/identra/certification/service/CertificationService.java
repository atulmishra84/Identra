package com.identra.certification.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CertificationService {

    private final Map<UUID, Map<String, Object>> campaigns = new ConcurrentHashMap<>();

    /** Simple SoD rule set for v3 bootstrap. */
    private static final List<Set<String>> SOD_CONFLICTS = List.of(
            Set.of("payroll-admin", "payroll-auditor"),
            Set.of("iam-admin", "iam-requester")
    );

    public Map<String, Object> createCampaign(UUID tenantId, String name, List<String> reviewers) {
        UUID id = UUID.randomUUID();
        Map<String, Object> campaign = new LinkedHashMap<>();
        campaign.put("id", id);
        campaign.put("tenantId", tenantId);
        campaign.put("name", name);
        campaign.put("status", "OPEN");
        campaign.put("reviewers", reviewers == null ? List.of() : reviewers);
        campaign.put("createdAt", Instant.now().toString());
        campaign.put("items", List.of(
                Map.of("identity", "jdoe", "entitlement", "payroll-admin", "decision", "PENDING"),
                Map.of("identity", "asmith", "entitlement", "iam-admin", "decision", "PENDING")
        ));
        campaigns.put(id, campaign);
        return campaign;
    }

    public Map<String, Object> get(UUID id) {
        Map<String, Object> campaign = campaigns.get(id);
        if (campaign == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }
        return campaign;
    }

    public Map<String, Object> evaluateSoD(List<String> entitlements) {
        List<String> violations = new ArrayList<>();
        for (Set<String> conflict : SOD_CONFLICTS) {
            if (entitlements != null && entitlements.containsAll(conflict)) {
                violations.add("SoD conflict: " + conflict);
            }
        }
        return Map.of(
                "entitlements", entitlements == null ? List.of() : entitlements,
                "violations", violations,
                "allowed", violations.isEmpty()
        );
    }

    public List<Map<String, Object>> list(UUID tenantId) {
        return campaigns.values().stream()
                .filter(c -> tenantId.equals(UUID.fromString(String.valueOf(c.get("tenantId")))))
                .toList();
    }
}
