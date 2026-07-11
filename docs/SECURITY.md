# Identra Security & AI Safety

## Security baseline

- OAuth2 / OIDC for portal and APIs; mTLS for service-to-service (mesh)
- Secrets in Azure Key Vault (or Vault); never in git or LLM prompts
- TLS in transit; AES-256 at rest for data stores
- PII scrubbing in logs; correlation via `traceId` / `tenantId` / `correlationId`
- Adapter credentials least-privilege and rotatable
- SBOM + signed images before production
- NetworkPolicies default-deny in Helm chart

## AI safety (post-MVP modules)

- No production secrets in prompts
- PII minimization / tokenization before LLM calls
- Structured outputs with schema validation
- Human approval when governance scores fall below threshold
- Prompt-injection defenses for marketplace RAG
- Provider kill switch via AI Gateway

## Password handling

- Never store passwords in Fabric (except Identity Core Argon2id when Core owns credentials)
- Short-lived KMS-enveloped payloads; purge after ACK
