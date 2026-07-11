# Identra

**Identra** is an enterprise-grade, AI-powered Identity Fabric Platform — vendor-neutral identity middleware between enterprise IAM products and downstream applications.

```text
Application → Identra → IAM Adapter → IAM Product
```

If an organization migrates from SailPoint to Saviynt (or Okta to Entra), only the adapter changes. Downstream applications keep working.

## Product pillars

| Module | Purpose |
|--------|---------|
| Identity Fabric | Universal Identity & Provisioning APIs, lifecycle, workflow, policy, audit |
| AI Connector Factory | Generate scored connectors from intent (v2) |
| Identity Core | Lightweight bootstrap IAM when no enterprise IAM exists (v2) |
| AI Automation Engine | RPA connectors for legacy/non-API systems (v3) |
| Migration Engine | Cross-IAM export / transform / import (v2) |
| Marketplace | Connectors, policies, templates (v2) |
| AI Governance | Quality, security, compliance scoring (v2) |

## MVP scope (Sprint 0–12)

- Universal SCIM/REST APIs
- Adapter SPI + **SailPoint ISC** + **Okta** adapters
- Provisioning jobs (Temporal), basic approvals
- PostgreSQL, Redis, Kafka, Elasticsearch
- Admin portal (Next.js)
- Azure AKS via Helm/Terraform

## Tech stack

- **Backend:** Java 21, Spring Boot 3.x (Gradle composite)
- **Frontend:** React + Next.js (pnpm)
- **Data:** PostgreSQL, Redis, Elasticsearch, Kafka
- **Workflow:** Temporal
- **Infra:** Docker, Kubernetes, Helm, Terraform (Azure-first)

## Repository layout

```text
apps/           # portal-web, admin-bff
services/       # microservices
libs/           # canonical-model, adapter-spi, security, common
adapters/       # okta, sailpoint-isc, identity-core
deploy/         # helm, terraform
docs/           # adr, openapi, standards
tools/          # simulators, scripts
```

## Quick start

### Prerequisites

- JDK 21+
- Node.js 22+ and pnpm 9+
- Docker (optional for local deps)

### Build backend

```bash
export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo /opt/homebrew/opt/openjdk)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew build
```

### Run identity + gateway (Sprint 1)

```bash
# Postgres required
docker compose -f deploy/docker/docker-compose.yml up -d postgres

./gradlew :services:identity-service:bootRun &
./gradlew :services:api-gateway:bootRun &

curl -s -X POST http://localhost:8081/v1/identities \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: 11111111-1111-1111-1111-111111111111" \
  -d '{"userName":"jdoe","active":true,"emails":[{"value":"jdoe@example.com","type":"work","primary":true}]}'
```

### Portal (when scaffolded)

```bash
pnpm install
pnpm --filter @identra/portal-web dev
```

### Local dependencies (optional)

```bash
docker compose -f deploy/docker/docker-compose.yml up -d
```

## Documentation

- [Architecture overview](docs/architecture/README.md)
- [ADRs](docs/adr/README.md)
- [OpenAPI](docs/openapi/README.md)
- [Development standards](docs/standards/development.md)
- [Coding standards](docs/standards/coding.md)
- [Security practices](docs/standards/security.md)
- [AI safety](docs/standards/ai-safety.md)

## License

Proprietary — All rights reserved.
