package com.identra.translation;

import com.identra.canonical.Identity;
import com.identra.canonical.MappingSet;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributeTranslatorTest {

    @Test
    void mapsCanonicalToVendor() {
        Instant now = Instant.now();
        Identity identity = new Identity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                "jdoe",
                List.of(new Identity.Email("jdoe@example.com", "work", true)),
                new Identity.Name("Jane Doe", "Doe", "Jane"),
                true,
                "Engineering",
                null,
                Identity.EmploymentStatus.ACTIVE,
                Map.of(),
                "identra",
                0,
                "W/\"1\"",
                now,
                now
        );
        Map<String, Object> vendor = AttributeTranslator.toVendor(identity, MappingSet.identityPassthrough("okta"));
        assertEquals("jdoe", vendor.get("userName"));
        assertEquals("jdoe@example.com", vendor.get("email"));
        assertTrue(Boolean.TRUE.equals(vendor.get("active")));
    }
}
