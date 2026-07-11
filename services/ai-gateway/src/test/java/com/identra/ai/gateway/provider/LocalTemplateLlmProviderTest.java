package com.identra.ai.gateway.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalTemplateLlmProviderTest {

    @Test
    void redactsSecrets() {
        String sanitized = LocalTemplateLlmProvider.sanitize("password=hunter2 Bearer abc.def api_key: xyz");
        assertFalse(sanitized.contains("hunter2"));
        assertFalse(sanitized.contains("abc.def"));
        assertTrue(sanitized.contains("[REDACTED]"));
    }
}
