# Identra OpenAPI

API-first contracts for the Universal Identity Fabric.

| Spec | Description |
|------|-------------|
| [identra-public-v1.yaml](identra-public-v1.yaml) | Public REST + SCIM surface (MVP) |
| [identra-admin-v1.yaml](identra-admin-v1.yaml) | Admin / operator APIs |
| [identra-provisioning-v1.yaml](identra-provisioning-v1.yaml) | Provisioning job APIs |

Internal service APIs will use gRPC (see `docs/protobuf/`).

## Versioning

- URI version: `/v1`, `/v2`
- SCIM at `/scim/v2`
- Errors: RFC 7807 `application/problem+json`
