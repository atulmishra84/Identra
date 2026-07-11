# Identra Architecture Overview

Identra is an identity fabric control plane. Downstream applications call Universal Identity and Provisioning APIs. The adapter plane talks to enterprise IAM products.

```text
Application → API Gateway → Identity / Provisioning Services
                                    ↓
                            Translation + Cache
                                    ↓
                            Adapter Runtime (SPI)
                                    ↓
                     Okta | SailPoint ISC | (future vendors)
```

## MVP services

| Service | Port | Role |
|---------|------|------|
| api-gateway | 8080 | Routing, future authn/z |
| identity-service | 8081 | Canonical identity API |
| provisioning-service | 8082 | Async provision jobs |
| tenant-service | 8083 | Tenant metadata |
| audit-service | 8084 | Audit event query |
| adapter-runtime | 8085 | Adapter registry + health |
| admin-bff | 8090 | Portal aggregation |

## Non-goals (MVP)

AI Connector Factory, Automation Engine, Migration Engine, Marketplace, full Identity Core IGA.
