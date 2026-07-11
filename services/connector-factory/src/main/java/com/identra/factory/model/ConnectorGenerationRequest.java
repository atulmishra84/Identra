package com.identra.factory.model;

import java.util.List;

public record ConnectorGenerationRequest(
        String iamPlatform,
        String application,
        String authenticationType,
        List<String> actions
) {
}
