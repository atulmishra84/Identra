# MVP Sprint Plan (Identra)

Confirmed defaults:

- **Repo:** `/Users/mac/identra` (monorepo `identra`)
- **MVP vendors:** Okta + SailPoint ISC

| Sprint | Focus |
|--------|--------|
| 0 (complete) | Monorepo, ADRs, OpenAPI, Helm, SPI, service skeletons, CI |
| 1–3 (in progress) | Gateway OIDC/JWKS, Redis identity cache (profile), tenant CRUD + Flyway, Temporal provisioning scaffold |
| 4–6 | Okta adapter live calls, Temporal→adapter-runtime dispatch, approvals |
| 7–9 | SailPoint ISC adapter + translation + swap demo |
| 10–12 | Portal polish, audit pipeline, hardening, pilot |

Out of scope until v2+: AI Connector Factory, Automation, Migration, Marketplace, full Identity Core.
