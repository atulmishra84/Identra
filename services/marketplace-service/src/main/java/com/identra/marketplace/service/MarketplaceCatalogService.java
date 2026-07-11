package com.identra.marketplace.service;

import com.identra.ai.GovernanceScorecard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketplaceCatalogService {

    private final Map<UUID, Map<String, Object>> catalog = new ConcurrentHashMap<>();
    private final boolean publicEnabled;
    private final String defaultVisibility;

    public MarketplaceCatalogService(
            @Value("${identra.marketplace.public-enabled:false}") boolean publicEnabled
    ) {
        this.publicEnabled = publicEnabled;
        this.defaultVisibility = publicEnabled ? "public" : "private-tenant";
        publishSeed("connector", "okta-saas-baseline", "Okta SaaS baseline connector pack");
        publishSeed("workflow", "jml-standard", "Standard joiner-mover-leaver workflow pack");
        publishSeed("policy", "least-privilege-starter", "Least privilege policy templates");
        publishSeed("automation", "sap-gui-joiner", "SAP GUI joiner automation pack");
    }

    public Map<String, Object> publish(
            String type,
            String name,
            String description,
            GovernanceScorecard scorecard,
            String visibility
    ) {
        if (scorecard != null && !scorecard.passes(70)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Artifact failed governance threshold: " + scorecard.gaps());
        }
        String resolvedVisibility = visibility == null || visibility.isBlank() ? defaultVisibility : visibility;
        if ("public".equalsIgnoreCase(resolvedVisibility) && !publicEnabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Public marketplace publishing disabled (set identra.marketplace.public-enabled=true)");
        }
        if (scorecard != null && "public".equalsIgnoreCase(resolvedVisibility) && scorecard.overallScore() < 85) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Public publish requires overall score >= 85");
        }
        UUID id = UUID.randomUUID();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("type", type);
        item.put("name", name);
        item.put("description", description);
        item.put("visibility", resolvedVisibility);
        item.put("signed", true);
        item.put("createdAt", Instant.now().toString());
        item.put("scorecard", scorecard);
        catalog.put(id, item);
        return item;
    }

    public List<Map<String, Object>> list(String type, String visibility) {
        return catalog.values().stream()
                .filter(i -> type == null || type.isBlank() || type.equals(i.get("type")))
                .filter(i -> visibility == null || visibility.isBlank() || visibility.equals(i.get("visibility")))
                .toList();
    }

    public Map<String, Object> get(UUID id) {
        Map<String, Object> item = catalog.get(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marketplace item not found");
        }
        return item;
    }

    public Map<String, Object> status() {
        return Map.of(
                "publicEnabled", publicEnabled,
                "defaultVisibility", defaultVisibility,
                "itemCount", catalog.size()
        );
    }

    private void publishSeed(String type, String name, String description) {
        publish(type, name, description,
                new GovernanceScorecard(90, 90, 90, 85, 80, 87, List.of(), List.of(), Map.of("seed", true)),
                defaultVisibility);
    }
}
