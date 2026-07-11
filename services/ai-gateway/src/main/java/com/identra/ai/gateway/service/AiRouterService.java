package com.identra.ai.gateway.service;

import com.identra.ai.LlmProvider;
import com.identra.ai.LlmRequest;
import com.identra.ai.LlmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AiRouterService {

    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final AtomicLong tokenBudgetUsed = new AtomicLong();
    private final long tokenBudget;
    private final String defaultProvider;

    public AiRouterService(
            List<LlmProvider> providerList,
            @Value("${identra.ai.default-provider:local}") String defaultProvider,
            @Value("${identra.ai.token-budget:100000}") long tokenBudget
    ) {
        providerList.forEach(p -> providers.put(p.id(), p));
        this.defaultProvider = defaultProvider;
        this.tokenBudget = tokenBudget;
    }

    public LlmResponse complete(LlmRequest request) {
        String providerId = request.provider() == null || request.provider().isBlank()
                ? defaultProvider
                : request.provider().toLowerCase(Locale.ROOT);
        LlmProvider provider = providers.get(providerId);
        if (provider == null) {
            provider = providers.values().stream()
                    .filter(p -> p.supports(request.model()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No LLM provider for " + providerId));
        }
        if (tokenBudgetUsed.get() >= tokenBudget) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "AI token budget exhausted");
        }
        LlmResponse response = provider.complete(request);
        tokenBudgetUsed.addAndGet(response.promptTokens() + response.completionTokens());
        return response;
    }

    public Map<String, Object> status() {
        return Map.of(
                "providers", providers.keySet(),
                "defaultProvider", defaultProvider,
                "tokenBudget", tokenBudget,
                "tokenBudgetUsed", tokenBudgetUsed.get()
        );
    }
}
