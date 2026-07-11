package com.identra.ai;

import java.util.List;
import java.util.Map;

/** Provider-neutral chat completion request. */
public record LlmRequest(
        String provider,
        String model,
        List<Message> messages,
        Double temperature,
        Integer maxTokens,
        Map<String, Object> metadata
) {
    public record Message(String role, String content) {
    }
}
