# MVP Sprint Plan (Identra)

Confirmed defaults:

- **Repo:** `/Users/mac/identra` (monorepo `identra`)
- **MVP vendors:** Okta + SailPoint ISC

| Sprint | Focus | Status |
|--------|--------|--------|
| 0 | Monorepo, ADRs, OpenAPI, Helm, SPI, CI | Complete |
| 1–3 | Identity CRUD/SCIM, OIDC/JWKS, Redis cache, tenant CRUD, Temporal scaffold | Complete |
| 4–6 | Okta adapter + adapter-runtime dispatch + approvals | Complete (dry-run default) |
| 7–9 | SailPoint ISC adapter + MappingSet translation + swap demo | Complete (dry-run default) |
| 10–12 | Portal ops console, audit PG store, security headers, pilot runbooks | Complete |

## Pilot checklist

See [docs/runbooks/pilot.md](../runbooks/pilot.md) and [docs/runbooks/swap-demo.md](../runbooks/swap-demo.md).

Out of scope until v2+: AI Connector Factory, Automation Engine, Migration Engine, Marketplace, full Identity Core IGA.
