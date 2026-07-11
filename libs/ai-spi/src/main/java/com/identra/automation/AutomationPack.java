package com.identra.automation;

import com.identra.ai.GovernanceScorecard;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AutomationPack(
        UUID id,
        String applicationName,
        AutomationTargetType targetType,
        AutomationToolchain toolchain,
        String status,
        Map<String, Object> artifacts,
        GovernanceScorecard scorecard,
        Instant createdAt
) {
}
