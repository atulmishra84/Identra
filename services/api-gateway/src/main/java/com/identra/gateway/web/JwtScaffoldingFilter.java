package com.identra.gateway.web;

import com.identra.security.TenantClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Dev-friendly JWT scaffolding: when identra.security.jwt-required=true,
 * require Bearer token and (best-effort) decode payload for tenant claim alignment.
 * Full JWKS validation is Sprint 2+.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 30)
public class JwtScaffoldingFilter extends OncePerRequestFilter {

    @Value("${identra.security.jwt-required:false}")
    private boolean jwtRequired;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/error") || !jwtRequired;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer token required");
            return;
        }
        String token = auth.substring("Bearer ".length()).trim();
        try {
            String payloadJson = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            Object tenantAttr = request.getAttribute("identra.tenantId");
            if (tenantAttr instanceof UUID tenantId
                    && payloadJson.contains("\"" + TenantClaims.TENANT_CLAIM + "\"")
                    && !payloadJson.contains(tenantId.toString())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token tenant does not match X-Tenant-Id");
                return;
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Bearer token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
