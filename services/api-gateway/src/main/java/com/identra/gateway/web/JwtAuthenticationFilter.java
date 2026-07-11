package com.identra.gateway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.identra.common.ProblemDetail;
import com.identra.security.IdentraPrincipal;
import com.identra.security.OidcJwtValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * When jwt-required=true, validates Bearer tokens via JWKS and enforces tid == X-Tenant-Id.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 30)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> SKIP_PREFIXES = Set.of("/actuator", "/health", "/error");

    private final ObjectProvider<OidcJwtValidator> validatorProvider;
    private final ObjectMapper objectMapper;
    private final boolean jwtRequired;

    public JwtAuthenticationFilter(
            ObjectProvider<OidcJwtValidator> validatorProvider,
            ObjectMapper objectMapper,
            @Value("${identra.security.jwt-required:false}") boolean jwtRequired
    ) {
        this.validatorProvider = validatorProvider;
        this.objectMapper = objectMapper;
        this.jwtRequired = jwtRequired;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !jwtRequired || SKIP_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        OidcJwtValidator validator = validatorProvider.getIfAvailable();
        if (validator == null) {
            writeProblem(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JWT validator not configured");
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeProblem(response, HttpServletResponse.SC_UNAUTHORIZED, "Bearer token required");
            return;
        }

        try {
            IdentraPrincipal principal = validator.validate(auth.substring("Bearer ".length()).trim());
            Object tenantAttr = request.getAttribute("identra.tenantId");
            if (tenantAttr instanceof UUID headerTenant && !headerTenant.equals(principal.tenantId())) {
                writeProblem(response, HttpServletResponse.SC_FORBIDDEN, "Token tenant does not match X-Tenant-Id");
                return;
            }
            request.setAttribute("identra.principal", principal);
            filterChain.doFilter(request, response);
        } catch (SecurityException ex) {
            writeProblem(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }
    }

    private void writeProblem(HttpServletResponse response, int status, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ProblemDetail(
                "about:blank",
                status == 403 ? "Forbidden" : (status == 401 ? "Unauthorized" : "Error"),
                status,
                detail,
                null,
                null
        ));
    }
}
