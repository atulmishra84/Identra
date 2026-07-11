# ADR-0007: Tenancy model

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture, Security

## Context

Customers require both multi-tenant SaaS economics and dedicated single-tenant isolation for regulated workloads.

## Decision

- **Same codebase**, two deployment profiles:
  - `saas-shared`: logical isolation (`tenant_id` + RLS)
  - `saas-dedicated`: dedicated DB/cluster option
- Control plane multi-tenant; data plane profile selectable per tenant SKU

## Consequences

- One product to maintain
- More complex ops and testing matrix
