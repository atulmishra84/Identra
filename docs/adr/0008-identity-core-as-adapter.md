# ADR-0008: Identity Core as adapter

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Product, Platform Architecture

## Context

Identity Core provides lightweight IAM when customers have no enterprise IAM. Special-casing Core would create dual orchestration paths.

## Decision

Identity Core implements the same **Adapter SPI** as SailPoint/Okta. Fabric orchestration is identical regardless of backend.

Marketed as **Bootstrap IAM**, not a SailPoint replacement.

## Consequences

- Single provisioning/workflow path
- Clearer product positioning
- Core features ship as an optional deployable adapter target
