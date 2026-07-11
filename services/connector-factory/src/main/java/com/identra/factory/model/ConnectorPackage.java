package com.identra.factory.model;

import com.identra.ai.GovernanceScorecard;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ConnectorPackage(
        UUID id,
        String iamPlatform,
        String application,
        String status,
        Map<String, Object> artifacts,
        GovernanceScorecard scorecard,
        Instant createdAt
) {
}
