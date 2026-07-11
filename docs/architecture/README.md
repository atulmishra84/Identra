# Identra Architecture (Sprint 0)

Identra is vendor-neutral identity middleware.

```text
Apps / SCIM clients
        ↓
   API Gateway
        ↓
 Identity / Provisioning / Tenant services
        ↓
   Adapter Runtime (SPI)
        ↓
 Okta | SailPoint ISC | Identity Core | …
```

## Key ADRs

See [docs/adr](../adr/README.md).

## MVP adapters

- Okta
- SailPoint ISC

## Deferred (v2+)

AI Connector Factory, Migration, Marketplace, Automation Engine, full Identity Core IGA.
