package com.identra.marketplace.web;

import com.identra.ai.GovernanceScorecard;
import com.identra.marketplace.service.MarketplaceCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/marketplace")
public class MarketplaceController {

    private final MarketplaceCatalogService catalogService;

    public MarketplaceController(MarketplaceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/items")
    public List<Map<String, Object>> list(@RequestParam(required = false) String type) {
        return catalogService.list(type);
    }

    @GetMapping("/items/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        return catalogService.get(id);
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> publish(@RequestBody Map<String, Object> body) {
        GovernanceScorecard scorecard = null;
        if (body.get("scorecard") instanceof Map<?, ?> raw) {
            scorecard = new GovernanceScorecard(
                    asInt(raw.get("coverageScore")),
                    asInt(raw.get("securityScore")),
                    asInt(raw.get("complianceScore")),
                    asInt(raw.get("performanceScore")),
                    asInt(raw.get("automationScore")),
                    asInt(raw.get("overallScore")),
                    List.of(),
                    List.of(),
                    Map.of()
            );
        }
        Map<String, Object> item = catalogService.publish(
                String.valueOf(body.getOrDefault("type", "connector")),
                String.valueOf(body.getOrDefault("name", "untitled")),
                String.valueOf(body.getOrDefault("description", "")),
                scorecard
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    private static int asInt(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        return 0;
    }
}
