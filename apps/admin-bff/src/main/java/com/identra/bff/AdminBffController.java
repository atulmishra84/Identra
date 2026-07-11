package com.identra.bff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/bff")
public class AdminBffController {

    private final RestClient restClient;
    private final String gatewayBaseUrl;

    public AdminBffController(
            RestClient.Builder builder,
            @Value("${identra.gateway-base-url:http://localhost:8080}") String gatewayBaseUrl
    ) {
        this.restClient = builder.build();
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @GetMapping("/health-summary")
    public Map<String, Object> healthSummary() {
        Object adapters = restClient.get()
                .uri(gatewayBaseUrl + "/v1/adapters")
                .retrieve()
                .body(Object.class);
        return Map.of(
                "product", "Identra",
                "adapters", adapters != null ? adapters : java.util.List.of()
        );
    }
}
