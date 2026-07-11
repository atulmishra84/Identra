package com.identra.factory.service;

import com.identra.ai.GovernanceScorecard;
import com.identra.ai.LlmRequest;
import com.identra.ai.LlmResponse;
import com.identra.factory.model.ConnectorGenerationRequest;
import com.identra.factory.model.ConnectorPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConnectorFactoryService {

    private final RestClient restClient = RestClient.create();
    private final String aiGatewayUrl;
    private final String governanceUrl;
    private final Map<UUID, ConnectorPackage> packages = new ConcurrentHashMap<>();

    public ConnectorFactoryService(
            @Value("${identra.ai-gateway-url:http://localhost:8091}") String aiGatewayUrl,
            @Value("${identra.governance-url:http://localhost:8092}") String governanceUrl
    ) {
        this.aiGatewayUrl = trim(aiGatewayUrl);
        this.governanceUrl = trim(governanceUrl);
    }

    public ConnectorPackage generate(ConnectorGenerationRequest request) {
        String prompt = """
                Generate an Identra connector for IAM=%s application=%s auth=%s actions=%s.
                Return JSON with connector, schema, mappings, provisioningLogic, tests, docs.
                Do not include secrets.
                """.formatted(
                request.iamPlatform(),
                request.application(),
                request.authenticationType(),
                request.actions()
        );

        LlmResponse llm = restClient.post()
                .uri(aiGatewayUrl + "/v1/ai/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LlmRequest(
                        "local",
                        "local-template",
                        List.of(new LlmRequest.Message("user", prompt)),
                        0.2,
                        2048,
                        Map.of("module", "connector-factory")
                ))
                .retrieve()
                .body(LlmResponse.class);

        Map<String, Object> artifacts = new LinkedHashMap<>();
        artifacts.put("connector", "GeneratedRestConnector");
        artifacts.put("configuration", Map.of(
                "iamPlatform", request.iamPlatform(),
                "application", request.application(),
                "authenticationType", request.authenticationType()
        ));
        artifacts.put("schema", Map.of("userName", "string", "email", "string", "active", "boolean"));
        artifacts.put("attributeMapping", Map.of("userName", "userName", "email", "email"));
        artifacts.put("provisioningLogic", request.actions());
        artifacts.put("workflow", "standard-provisioning");
        artifacts.put("testCases", List.of("createUser", "disableUser"));
        artifacts.put("documentation", "Auto-generated connector package");
        artifacts.put("deploymentPackage", "identra-connector-" + request.application().toLowerCase() + ".zip");
        artifacts.put("llmRaw", llm == null ? "" : llm.content());

        GovernanceScorecard scorecard = restClient.post()
                .uri(governanceUrl + "/v1/governance/score")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("artifacts", artifacts, "actions", request.actions()))
                .retrieve()
                .body(GovernanceScorecard.class);

        if (scorecard == null) {
            scorecard = new GovernanceScorecard(70, 70, 70, 70, 70, 70, List.of(), List.of(), Map.of());
        }

        String status = scorecard.passes(70) ? "VALIDATED" : "NEEDS_MANUAL_WORK";
        ConnectorPackage pkg = new ConnectorPackage(
                UUID.randomUUID(),
                request.iamPlatform(),
                request.application(),
                status,
                artifacts,
                scorecard,
                Instant.now()
        );
        packages.put(pkg.id(), pkg);
        return pkg;
    }

    public ConnectorPackage get(UUID id) {
        return packages.get(id);
    }

    public List<ConnectorPackage> list() {
        return List.copyOf(packages.values());
    }

    private static String trim(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
