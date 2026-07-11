# ADR-0004: Adapter SPI and isolation tiers

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture, Security

## Context

In-process plugins are fast but expand blast radius for marketplace/partner connectors. First-party adapters need lower latency.

## Decision

- Define a Java **Adapter SPI** in `libs/adapter-spi`
- **Tier A (first-party):** in-process plugins (Okta, SailPoint ISC, Identity Core)
- **Tier B (partner/marketplace):** out-of-process gRPC sidecars with signed manifests, network policies, scoped secrets
- Capability negotiation via `CapabilitiesDescriptor`

## Consequences

- Safer marketplace model
- Two execution paths to maintain
- Prefer security over micro-optimizing latency for untrusted code
