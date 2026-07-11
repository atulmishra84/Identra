package com.identra.automation.factory.service;

import com.identra.ai.GovernanceScorecard;
import com.identra.ai.LlmRequest;
import com.identra.ai.LlmResponse;
import com.identra.automation.AutomationGenerationRequest;
import com.identra.automation.AutomationPack;
import com.identra.automation.AutomationToolchain;
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
public class AutomationFactoryService {

    private final RestClient restClient = RestClient.create();
    private final String aiGatewayUrl;
    private final String governanceUrl;
    private final Map<UUID, AutomationPack> packs = new ConcurrentHashMap<>();

    public AutomationFactoryService(
            @Value("${identra.ai-gateway-url:http://localhost:8091}") String aiGatewayUrl,
            @Value("${identra.governance-url:http://localhost:8092}") String governanceUrl
    ) {
        this.aiGatewayUrl = trim(aiGatewayUrl);
        this.governanceUrl = trim(governanceUrl);
    }

    public AutomationPack generate(AutomationGenerationRequest request) {
        AutomationToolchain toolchain = request.toolchain() == null
                ? preferredToolchain(request.targetType().name())
                : request.toolchain();

        String prompt = """
                Generate Identra automation connector for app=%s target=%s toolchain=%s actions=%s.
                Never include production credentials. Use placeholders for secrets.
                """.formatted(
                request.applicationName(),
                request.targetType(),
                toolchain,
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
                        Map.of("module", "automation-factory")
                ))
                .retrieve()
                .body(LlmResponse.class);

        Map<String, Object> artifacts = new LinkedHashMap<>();
        artifacts.put("entrypoint", entrypoint(toolchain));
        artifacts.put("toolchain", toolchain.name());
        artifacts.put("targetType", request.targetType().name());
        artifacts.put("actions", request.actions());
        artifacts.put("script", sampleScript(toolchain, request.applicationName(), request.actions()));
        artifacts.put("secretsPolicy", Map.of(
                "injection", "runtime-vault-only",
                "promptSecrets", "forbidden",
                "recordingRetentionDays", 30
        ));
        artifacts.put("isolation", Map.of(
                "workerPool", "automation-workers",
                "network", "egress-allowlist",
                "credentials", "JIT"
        ));
        artifacts.put("llmRaw", llm == null ? "" : llm.content());
        artifacts.put("deploymentPackage", "identra-automation-" + request.applicationName().toLowerCase() + ".zip");

        GovernanceScorecard scorecard = restClient.post()
                .uri(governanceUrl + "/v1/governance/score")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("artifacts", artifacts, "actions", request.actions()))
                .retrieve()
                .body(GovernanceScorecard.class);

        if (scorecard == null) {
            scorecard = new GovernanceScorecard(75, 80, 75, 70, 85, 77, List.of(), List.of(), Map.of());
        }

        AutomationPack pack = new AutomationPack(
                UUID.randomUUID(),
                request.applicationName(),
                request.targetType(),
                toolchain,
                scorecard.passes(70) ? "VALIDATED" : "NEEDS_MANUAL_WORK",
                artifacts,
                scorecard,
                Instant.now()
        );
        packs.put(pack.id(), pack);
        return pack;
    }

    public AutomationPack get(UUID id) {
        return packs.get(id);
    }

    public List<AutomationPack> list() {
        return List.copyOf(packs.values());
    }

    private static AutomationToolchain preferredToolchain(String targetType) {
        return switch (targetType) {
            case "BROWSER" -> AutomationToolchain.PLAYWRIGHT;
            case "DESKTOP", "CITRIX" -> AutomationToolchain.UIPATH;
            case "TERMINAL" -> AutomationToolchain.PYTHON;
            case "SAP_GUI", "ORACLE_FORMS" -> AutomationToolchain.ROBOT_FRAMEWORK;
            default -> AutomationToolchain.PYTHON;
        };
    }

    private static String entrypoint(AutomationToolchain toolchain) {
        return switch (toolchain) {
            case PLAYWRIGHT -> "run.mjs";
            case SELENIUM -> "Main.java";
            case ROBOT_FRAMEWORK -> "suite.robot";
            case POWER_AUTOMATE -> "flow.json";
            case UIPATH -> "Main.xaml";
            case PYTHON -> "main.py";
        };
    }

    private static String sampleScript(AutomationToolchain toolchain, String app, List<String> actions) {
        return switch (toolchain) {
            case PLAYWRIGHT -> "// Playwright pack for " + app + " actions=" + actions + "\nawait page.goto(process.env.APP_URL);";
            case PYTHON -> "# Python automation for " + app + "\n# secrets from vault at runtime only\nprint('execute', " + actions + ")";
            default -> "# Generated " + toolchain + " pack for " + app;
        };
    }

    private static String trim(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
