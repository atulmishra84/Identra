package com.identra.governance.service;

import com.identra.ai.GovernanceScorecard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GovernanceScoringService {

    @SuppressWarnings("unchecked")
    public GovernanceScorecard score(Map<String, Object> body) {
        Map<String, Object> artifacts = body.get("artifacts") instanceof Map<?, ?> m
                ? (Map<String, Object>) m
                : Map.of();
        List<String> actions = body.get("actions") instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();

        List<String> gaps = new ArrayList<>();
        int coverage = Math.min(100, 40 + actions.size() * 10);
        if (!artifacts.containsKey("testCases")) {
            gaps.add("Missing testCases artifact");
            coverage -= 10;
        }
        if (!artifacts.containsKey("documentation")) {
            gaps.add("Missing documentation artifact");
            coverage -= 5;
        }

        int security = 85;
        String raw = String.valueOf(artifacts.getOrDefault("llmRaw", ""));
        if (raw.toLowerCase().contains("password") && !raw.contains("[REDACTED]")) {
            security = 40;
            gaps.add("Possible secret leakage in generated content");
        }

        int compliance = artifacts.containsKey("attributeMapping") ? 80 : 55;
        int performance = 75;
        int automation = artifacts.containsKey("provisioningLogic") ? 80 : 50;
        int overall = (coverage + security + compliance + performance + automation) / 5;

        List<String> recommendations = new ArrayList<>();
        if (overall < 70) {
            recommendations.add("Complete missing artifacts before publishing to Marketplace");
        }
        if (security < 70) {
            recommendations.add("Re-run generation with secret scrubbing enabled");
        }
        if (actions.isEmpty()) {
            gaps.add("No actions selected");
            recommendations.add("Select at least Create/Disable/Delete actions");
        }

        return new GovernanceScorecard(
                clamp(coverage),
                clamp(security),
                clamp(compliance),
                clamp(performance),
                clamp(automation),
                clamp(overall),
                gaps,
                recommendations,
                Map.of("artifactKeys", artifacts.keySet())
        );
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
