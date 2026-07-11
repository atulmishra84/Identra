package com.identra.translation;

import com.identra.canonical.Identity;
import com.identra.canonical.MappingSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Translates between canonical Identra identities and vendor attribute maps.
 */
public final class AttributeTranslator {

    private AttributeTranslator() {
    }

    public static Map<String, Object> toVendor(Identity identity, MappingSet mapping) {
        Map<String, Object> vendor = new HashMap<>();
        put(vendor, mapping.canonicalToVendor().get("userName"), identity.userName());
        if (identity.emails() != null && !identity.emails().isEmpty()) {
            put(vendor, mapping.canonicalToVendor().get("emails[0].value"), identity.emails().getFirst().value());
        }
        if (identity.name() != null) {
            put(vendor, mapping.canonicalToVendor().get("name.givenName"), identity.name().givenName());
            put(vendor, mapping.canonicalToVendor().get("name.familyName"), identity.name().familyName());
        }
        put(vendor, mapping.canonicalToVendor().get("active"), identity.active());
        put(vendor, mapping.canonicalToVendor().get("department"), identity.department());
        return vendor;
    }

    public static Identity.Name nameFromVendor(Map<String, Object> vendor, MappingSet mapping) {
        String given = string(vendor.get(mapping.canonicalToVendor().get("name.givenName")));
        // prefer reverse map keys when reading
        if (given == null) {
            given = string(vendor.get("firstName"));
        }
        String family = string(vendor.get("lastName"));
        String formatted = ((given == null ? "" : given) + " " + (family == null ? "" : family)).trim();
        return new Identity.Name(formatted.isEmpty() ? null : formatted, family, given);
    }

    public static List<Identity.Email> emailsFromVendor(Map<String, Object> vendor) {
        String email = string(vendor.get("email"));
        if (email == null) {
            Object profile = vendor.get("profile");
            if (profile instanceof Map<?, ?> profileMap) {
                email = string(profileMap.get("email"));
            }
        }
        if (email == null) {
            return List.of();
        }
        return List.of(new Identity.Email(email, "work", true));
    }

    private static void put(Map<String, Object> map, String key, Object value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }

    private static String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
