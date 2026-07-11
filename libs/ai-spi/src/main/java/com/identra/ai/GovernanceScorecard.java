package com.identra.ai;

import java.util.List;
import java.util.Map;

/** Connector quality scorecard from AI Governance. */
public record GovernanceScorecard(
        int coverageScore,
        int securityScore,
        int complianceScore,
        int performanceScore,
        int automationScore,
        int overallScore,
        List<String> gaps,
        List<String> recommendations,
        Map<String, Object> details
) {
    public boolean passes(int threshold) {
        return overallScore >= threshold;
    }
}
