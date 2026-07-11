package com.identra.gateway.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

@RestController
public class GatewayProxyController {

    private final RestClient restClient = RestClient.create();

    @Value("${identra.routes.identity:http://localhost:8081}")
    private String identityBase;

    @Value("${identra.routes.provisioning:http://localhost:8082}")
    private String provisioningBase;

    @Value("${identra.routes.tenant:http://localhost:8083}")
    private String tenantBase;

    @Value("${identra.routes.audit:http://localhost:8084}")
    private String auditBase;

    @Value("${identra.routes.adapters:http://localhost:8085}")
    private String adaptersBase;

    @Value("${identra.routes.approvals:http://localhost:8086}")
    private String approvalsBase;

    @Value("${identra.routes.ai:http://localhost:8091}")
    private String aiBase;

    @Value("${identra.routes.governance:http://localhost:8092}")
    private String governanceBase;

    @Value("${identra.routes.connectorFactory:http://localhost:8093}")
    private String connectorFactoryBase;

    @Value("${identra.routes.identityCore:http://localhost:8094}")
    private String identityCoreBase;

    @Value("${identra.routes.migration:http://localhost:8095}")
    private String migrationBase;

    @Value("${identra.routes.marketplace:http://localhost:8096}")
    private String marketplaceBase;

    @Value("${identra.routes.automationFactory:http://localhost:8097}")
    private String automationFactoryBase;

    @Value("${identra.routes.automationRuntime:http://localhost:8098}")
    private String automationRuntimeBase;

    @Value("${identra.routes.certification:http://localhost:8099}")
    private String certificationBase;

    @RequestMapping({
            "/v1/identities",
            "/v1/identities/**",
            "/scim/v2/**",
            "/v1/provisioning/**",
            "/v1/tenants/**",
            "/v1/audit/**",
            "/v1/adapters/**",
            "/v1/approvals",
            "/v1/approvals/**",
            "/v1/ai/**",
            "/v1/governance/**",
            "/v1/core/**",
            "/v1/migrations/**",
            "/v1/marketplace/**",
            "/v1/automation/**",
            "/v1/certifications/**"
    })
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws java.io.IOException {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String base = resolveBase(path);
        String target = base + path + (query == null ? "" : "?" + query);

        RestClient.RequestBodySpec spec = restClient.method(org.springframework.http.HttpMethod.valueOf(request.getMethod()))
                .uri(URI.create(target));

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (isHopByHop(name)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                spec = spec.header(name, values.nextElement());
            }
        }

        byte[] requestBody = request.getInputStream().readAllBytes();
        return (requestBody.length == 0 ? spec : spec.body(requestBody)).exchange((req, res) -> {
            byte[] body = res.getBody().readAllBytes();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            res.getHeaders().forEach((name, values) -> {
                if (!isHopByHop(name)) {
                    headers.put(name, values);
                }
            });
            return ResponseEntity.status(res.getStatusCode())
                    .headers(headers)
                    .body(body);
        });
    }

    private static boolean isHopByHop(String name) {
        return "host".equalsIgnoreCase(name)
                || "content-length".equalsIgnoreCase(name)
                || "transfer-encoding".equalsIgnoreCase(name)
                || "connection".equalsIgnoreCase(name)
                || "keep-alive".equalsIgnoreCase(name)
                || "proxy-authenticate".equalsIgnoreCase(name)
                || "proxy-authorization".equalsIgnoreCase(name)
                || "te".equalsIgnoreCase(name)
                || "trailers".equalsIgnoreCase(name)
                || "upgrade".equalsIgnoreCase(name);
    }

    private String resolveBase(String path) {
        if (path.startsWith("/v1/identities") || path.startsWith("/scim/")) {
            return identityBase;
        }
        if (path.startsWith("/v1/provisioning")) {
            return provisioningBase;
        }
        if (path.startsWith("/v1/tenants")) {
            return tenantBase;
        }
        if (path.startsWith("/v1/audit")) {
            return auditBase;
        }
        if (path.startsWith("/v1/adapters")) {
            return adaptersBase;
        }
        if (path.startsWith("/v1/approvals")) {
            return approvalsBase;
        }
        if (path.startsWith("/v1/ai/connector-factory")) {
            return connectorFactoryBase;
        }
        if (path.startsWith("/v1/ai/automation-factory")) {
            return automationFactoryBase;
        }
        if (path.startsWith("/v1/ai")) {
            return aiBase;
        }
        if (path.startsWith("/v1/governance")) {
            return governanceBase;
        }
        if (path.startsWith("/v1/core")) {
            return identityCoreBase;
        }
        if (path.startsWith("/v1/migrations")) {
            return migrationBase;
        }
        if (path.startsWith("/v1/marketplace")) {
            return marketplaceBase;
        }
        if (path.startsWith("/v1/automation")) {
            return automationRuntimeBase;
        }
        if (path.startsWith("/v1/certifications")) {
            return certificationBase;
        }
        throw new IllegalArgumentException("No upstream for path " + path);
    }
}
