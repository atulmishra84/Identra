package com.identra.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Validates OIDC access tokens against a JWKS endpoint and extracts Identra claims.
 */
public final class OidcJwtValidator {

    private final ConfigurableJWTProcessor<SecurityContext> processor;
    private final String expectedIssuer;
    private final String expectedAudience;

    public OidcJwtValidator(String jwkSetUri, String expectedIssuer, String expectedAudience) {
        this.expectedIssuer = Objects.requireNonNull(expectedIssuer, "issuer");
        this.expectedAudience = expectedAudience;
        try {
            JWKSource<SecurityContext> keySource = JWKSourceBuilder
                    .create(URI.create(jwkSetUri).toURL())
                    .retrying(true)
                    .build();
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
            jwtProcessor.setJWSKeySelector(keySelector);
            this.processor = jwtProcessor;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize JWKS validator for " + jwkSetUri, e);
        }
    }

    public IdentraPrincipal validate(String bearerToken) {
        try {
            JWTClaimsSet claims = processor.process(bearerToken, null);
            validateIssuer(claims);
            validateAudience(claims);
            validateExpiry(claims);

            UUID tenantId = parseTenant(claims);
            String subject = claims.getSubject();
            List<String> roles = claims.getStringListClaim(TenantClaims.ROLES_CLAIM);
            return new IdentraPrincipal(subject, tenantId, roles == null ? List.of() : roles);
        } catch (ParseException e) {
            throw new SecurityException("Invalid JWT claims", e);
        } catch (com.nimbusds.jose.proc.BadJOSEException | com.nimbusds.jose.JOSEException e) {
            throw new SecurityException("JWT validation failed", e);
        }
    }

    private void validateIssuer(JWTClaimsSet claims) throws ParseException {
        if (!expectedIssuer.equals(claims.getIssuer())) {
            throw new SecurityException("Unexpected issuer: " + claims.getIssuer());
        }
    }

    private void validateAudience(JWTClaimsSet claims) {
        if (expectedAudience == null || expectedAudience.isBlank()) {
            return;
        }
        List<String> audiences = claims.getAudience();
        if (audiences == null || audiences.stream().noneMatch(expectedAudience::equals)) {
            throw new SecurityException("Token audience does not include " + expectedAudience);
        }
    }

    private void validateExpiry(JWTClaimsSet claims) {
        if (claims.getExpirationTime() != null
                && claims.getExpirationTime().toInstant().isBefore(Instant.now())) {
            throw new SecurityException("Token expired");
        }
    }

    private static UUID parseTenant(JWTClaimsSet claims) throws ParseException {
        String tid = claims.getStringClaim(TenantClaims.TENANT_CLAIM);
        if (tid == null || tid.isBlank()) {
            Object nested = claims.getClaim("tenant_id");
            tid = nested == null ? null : String.valueOf(nested);
        }
        if (tid == null || tid.isBlank()) {
            throw new SecurityException("Missing tenant claim (" + TenantClaims.TENANT_CLAIM + ")");
        }
        try {
            return UUID.fromString(tid);
        } catch (IllegalArgumentException ex) {
            throw new SecurityException("Tenant claim is not a UUID");
        }
    }
}
