package com.identra.canonical;

import java.util.Map;

/**
 * Versioned attribute mapping between canonical Identra fields and a vendor schema.
 */
public record MappingSet(
        String id,
        String adapterId,
        String version,
        Map<String, String> canonicalToVendor,
        Map<String, String> vendorToCanonical
) {
    public static MappingSet identityPassthrough(String adapterId) {
        return new MappingSet(
                adapterId + "-passthrough",
                adapterId,
                "1.0.0",
                Map.of(
                        "userName", "userName",
                        "emails[0].value", "email",
                        "name.givenName", "firstName",
                        "name.familyName", "lastName",
                        "active", "active",
                        "department", "department"
                ),
                Map.of(
                        "userName", "userName",
                        "email", "emails[0].value",
                        "firstName", "name.givenName",
                        "lastName", "name.familyName",
                        "active", "active",
                        "department", "department"
                )
        );
    }
}
