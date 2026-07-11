package com.identra.adapter.sailpoint.isc;

public record SailPointIscClientConfig(
        String baseUrl,
        String clientId,
        String clientSecret,
        boolean dryRun
) {
    public static SailPointIscClientConfig dryRunDefaults() {
        return new SailPointIscClientConfig("https://example.api.identitynow.com", "", "", true);
    }
}
