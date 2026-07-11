package com.identra.adapter.okta;

/**
 * Connection settings for Okta org APIs.
 * When dryRun=true, no remote calls are made (local/dev/simulator).
 */
public record OktaClientConfig(
        String orgUrl,
        String apiToken,
        boolean dryRun
) {
    public static OktaClientConfig dryRunDefaults() {
        return new OktaClientConfig("https://example.okta.com", "", true);
    }
}
