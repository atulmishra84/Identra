package com.identra.marketplace.service;

import com.identra.ai.GovernanceScorecard;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketplaceCatalogService {

    private final Map<UUID, Map<String, Object>> catalog = new ConcurrentHashMap<>();

    public MarketplaceCatalogService() {
        publishSeed("connector", "okta-saas-baseline", "Okta SaaS baseline connector pack");
        publishSeed("workflow", "jml-standard", "Standard joiner-mover-leaver workflow pack");
        publishSeed("policy", "least-privilege-starter", "Least privilege policy templates");
    }

    public Map<String, Object> publish(String type, String name, String description, GovernanceScorecard scorecard) {
        if (scorecard != null && !scorecard.passes(70)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Artifact failed governance threshold: " + scorecard.gaps());
        }
        UUID id = UUID.randomUUID();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("type", type);
        item.put("name", name);
        item.put("description", description);
        item.put("visibility", "private-tenant");
        item.put("signed", true);
        item.put("createdAt", Instant.now().toString());
        item.put("scorecard", scorecard);
        catalog.put(id, item);
        return item;
    }

    public List<Map<String, Object>> list(String type) {
        return catalog.values().stream()
                .filter(i -> type == null || type.isBlank() || type.equals(i.get("type")))
                .toList();
    }

    public Map<String, Object> get(UUID id) {
        Map<String, Object> item = catalog.get(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marketplace item not found");
        }
        return item;
    }

    private void publishSeed(String type, String name, String description) {
        publish(type, name, description, new GovernanceScorecard(90, 90, 90, 85, 80, 87, List.of(), List.of(), Map.of("seed", true)));
    }
}
