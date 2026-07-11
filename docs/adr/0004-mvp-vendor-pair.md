# ADR 0004: MVP Vendor Pair — Okta + SailPoint ISC

## Status

Accepted

## Context

Supporting ten IAM products at once delays a working portability demo.

## Decision

MVP adapters: **Okta** and **SailPoint Identity Security Cloud (ISC)**. Success metric: swap adapter binding without changing application clients.

## Consequences

- Simulators under `tools/simulators/{okta,sailpoint-isc}`
- Other vendors deferred to v2/v3 roadmap
- Identity Core adapter remains a placeholder until Bootstrap IAM module lands
