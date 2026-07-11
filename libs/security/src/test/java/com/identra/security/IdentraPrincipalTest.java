package com.identra.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentraPrincipalTest {

    @Test
    void hasRoleIsCaseInsensitive() {
        IdentraPrincipal principal = new IdentraPrincipal(
                "user-1",
                java.util.UUID.randomUUID(),
                java.util.List.of("TenantAdmin")
        );
        assertTrue(principal.hasRole("tenantadmin"));
        assertEquals("user-1", principal.subject());
    }
}
