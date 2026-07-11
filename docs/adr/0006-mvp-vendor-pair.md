# ADR-0006: MVP vendor pair ISC + Okta

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Product, Platform Architecture

## Context

Supporting ten IAM vendors at once delays learning and demoability. Highest demand pair for portability demos is SailPoint ISC and Okta.

## Decision

MVP adapters:

1. **SailPoint Identity Security Cloud (ISC)**
2. **Okta**

Success criterion: swap Okta ↔ ISC without changing the application integration.

Additional vendors (Saviynt, Entra, IIQ, Ping, etc.) land in v2/v3.

## Consequences

- Focused MVP
- Deferred coverage of other enterprise IAMs
