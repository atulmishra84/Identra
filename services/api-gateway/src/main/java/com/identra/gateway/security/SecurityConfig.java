package com.identra.gateway.security;

import com.identra.security.OidcJwtValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    @ConditionalOnProperty(name = "identra.security.jwt-required", havingValue = "true")
    public OidcJwtValidator oidcJwtValidator(
            @Value("${identra.security.jwk-set-uri}") String jwkSetUri,
            @Value("${identra.security.issuer-uri}") String issuerUri,
            @Value("${identra.security.audience:}") String audience
    ) {
        return new OidcJwtValidator(jwkSetUri, issuerUri, audience);
    }
}
