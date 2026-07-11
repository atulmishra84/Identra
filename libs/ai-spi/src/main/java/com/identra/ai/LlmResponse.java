package com.identra.ai;

import java.util.Map;

public record LlmResponse(
        String provider,
        String model,
        String content,
        int promptTokens,
        int completionTokens,
        Map<String, Object> metadata
) {
}
