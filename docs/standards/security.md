# Security Best Practices

- Least privilege for cloud roles and adapter credentials
- Secrets in Azure Key Vault / Vault — never in git
- Secret scanning in CI
- Adapter credential rotation
- Threat model (STRIDE) per epic
- Encryption in transit (TLS 1.2+) and at rest
- Passwords never stored in Fabric (except Identity Core Argon2id when Core owns credentials)
- DLP on AI prompts (no raw HR dumps / production secrets)
- Signed images and SBOMs before production
