package com.identra.ai.gateway.provider;

import com.identra.ai.LlmProvider;
import com.identra.ai.LlmRequest;
import com.identra.ai.LlmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Air-gapped provider placeholder for Llama/Mistral running on private GPU/CPU nodes.
 * Enabled with identra.ai.airgap-enabled=true — never calls public internet.
 */
@Component
@ConditionalOnProperty(name = "identra.ai.airgap-enabled", havingValue = "true")
public class AirgappedLlmProvider implements LlmProvider {

    private static final Set<String> MODELS = Set.of("llama-3.1-70b", "mistral-large", "llama-local", "mistral-local");

    private final String endpoint;

    public AirgappedLlmProvider(@Value("${identra.ai.airgap-endpoint:http://llm.internal:8080}") String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String id() {
        return "airgap";
    }

    @Override
    public boolean supports(String model) {
        return model != null && MODELS.contains(model.toLowerCase(Locale.ROOT));
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        String sanitized = LocalTemplateLlmProvider.sanitize(
                request.messages() == null || request.messages().isEmpty()
                        ? ""
                        : request.messages().getLast().content()
        );
        // Offline-safe deterministic response — swap for private HTTP call to endpoint later.
        String content = """
                {"mode":"airgap","endpoint":"%s","status":"generated-offline","promptDigest":"%s"}
                """.formatted(endpoint, Integer.toHexString(sanitized.hashCode()));
        return new LlmResponse(
                id(),
                request.model() == null ? "llama-3.1-70b" : request.model(),
                content,
                sanitized.length() / 4,
                content.length() / 4,
                Map.of("network", "private", "egress", "denied")
        );
    }
}
