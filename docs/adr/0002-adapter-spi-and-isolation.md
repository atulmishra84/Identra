# ADR 0002: Adapter SPI and Plugin Isolation

## Status

Accepted

## Context

Adapters translate Identra’s canonical model to vendor APIs. Marketplace and partner connectors are less trusted than first-party adapters.

## Decision

- Define a shared **Adapter SPI** in `libs/adapter-spi` (`IdentityAdapter`, `EntitlementAdapter`, `PasswordAdapter`, `SyncAdapter`, `HealthCapable`, `CapabilitiesDescriptor`)
- **Tier A (first-party):** in-process libraries (`adapters/okta`, `adapters/sailpoint-isc`) loaded by `adapter-runtime`
- **Tier B (partner/marketplace):** out-of-process gRPC sidecars with signed manifests (post-MVP)

## Consequences

- No vendor types outside `adapters/*`
- Capability negotiation avoids assuming uniform vendor features
- Stronger blast-radius control for untrusted connectors later
