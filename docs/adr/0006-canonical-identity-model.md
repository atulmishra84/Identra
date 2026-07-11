# ADR 0006: Canonical Identity Model + SCIM

## Status

Accepted

## Context

Applications must not depend on SailPoint/Okta schemas.

## Decision

- Canonical model in `libs/canonical-model` (`Identity`, `ProvisioningRequest`, `ProvisioningJob`, …)
- Public APIs: SCIM 2.0 subset + `/v1/*` Identra extensions
- Vendor-specific fields only via discouraged extension URN: `urn:identra:params:scim:schemas:extension:vendor:2.0:*`
- Attribute mapping via versioned Mapping Sets (JSONata/JQ-style transforms)

## Consequences

- Translation layer sits between Universal API and adapters
- OpenAPI is source of truth under `docs/openapi`
