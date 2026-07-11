# ADR-0003: Canonical identity model

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture

## Context

Applications must not depend on SailPoint/Okta/Saviynt schemas. Vendor lock-in is the primary problem Identra solves.

## Decision

- All public APIs expose a **canonical identity model** (SCIM 2.0 + Identra extensions)
- Vendor attributes map only through versioned **MappingSets**
- Vendor-specific fields must not leak to apps except via discouraged extension URN:
  `urn:identra:params:scim:schemas:extension:vendor:2.0:*`
- No vendor types outside `adapters/*` modules

## Consequences

- Adapter swap without application rewrites
- Mapping maintenance cost per vendor
- Strict module boundaries enforced in CI
