# ADR 0005: Tenancy Model

## Status

Accepted

## Context

Customers need SaaS multi-tenancy and regulated dedicated deployments.

## Decision

Same codebase, two deployment profiles:

1. **saas-shared** — logical isolation (`tenant_id` + RLS)
2. **saas-dedicated** — dedicated DB/cluster option

## Consequences

- Tenant resolved from JWT claim and/or `X-Tenant-Id` (must match)
- Feature flags and quotas per tenant
- Air-gap / on-prem profile deferred to v3
