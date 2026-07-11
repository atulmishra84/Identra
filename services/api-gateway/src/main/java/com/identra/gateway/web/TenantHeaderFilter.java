package com.identra.gateway.web;

import com.identra.common.ProblemDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Edge tenant gate: require X-Tenant-Id on API calls (Sprint 1).
 * JWT claim matching lands in a follow-up once OIDC is wired.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class TenantHeaderFilter extends OncePerRequestFilter {

    private static final Set<String> SKIP_PREFIXES = Set.of(
            "/actuator",
            "/health",
            "/v1/health",
            "/error"
    );

    private final ObjectMapper objectMapper;

    public TenantHeaderFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String tenantHeader = request.getHeader("X-Tenant-Id");
        if (tenantHeader == null || tenantHeader.isBlank()) {
            writeProblem(response, HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-Id header");
            return;
        }
        try {
            UUID tenantId = UUID.fromString(tenantHeader.trim());
            MDC.put("tenantId", tenantId.toString());
            request.setAttribute("identra.tenantId", tenantId);
        } catch (IllegalArgumentException ex) {
            writeProblem(response, HttpServletResponse.SC_BAD_REQUEST, "X-Tenant-Id must be a UUID");
            return;
        }

        String correlationId = request.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);
        response.setHeader("X-Correlation-Id", correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("tenantId");
            MDC.remove("correlationId");
        }
    }

    private void writeProblem(HttpServletResponse response, int status, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ProblemDetail(
                "about:blank",
                status == 400 ? "Bad Request" : "Unauthorized",
                status,
                detail,
                null,
                null
        ));
    }
}
