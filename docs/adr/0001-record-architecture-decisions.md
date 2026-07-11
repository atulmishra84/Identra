# ADR-0001: Record architecture decisions

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture

## Context

Identra is a long-lived commercial platform. Decisions about stack, tenancy, adapters, and AI safety must be durable and discoverable.

## Decision

We will use Architecture Decision Records (ADRs) in `docs/adr/` for every cross-cutting technical decision. ADRs are required before implementing significant platform changes.

## Consequences

- Traceable rationale for onboarding and audits
- Slight process overhead on large changes
