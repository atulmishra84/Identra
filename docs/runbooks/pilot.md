# Identra MVP Pilot Runbook

## Objectives

1. Apps integrate only with Identra Universal API / SCIM
2. Provisioning reaches Okta and SailPoint ISC through adapters
3. Adapter swap does not change application payloads
4. Audit events are durable in PostgreSQL
5. Approvals can gate high-risk jobs when enabled

## Bring-up (local)

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
docker compose -f deploy/docker/docker-compose.yml up -d postgres redis

./gradlew :services:identity-service:bootRun &
./gradlew :services:tenant-service:bootRun &
./gradlew :services:provisioning-service:bootRun &
./gradlew :services:adapter-runtime:bootRun &
./gradlew :services:audit-service:bootRun &
./gradlew :services:approval-service:bootRun &
./gradlew :services:api-gateway:bootRun &
```

## Smoke tests

```bash
TENANT=11111111-1111-1111-1111-111111111111

# Identity
curl -s -X POST localhost:8081/v1/identities \
  -H "Content-Type: application/json" -H "X-Tenant-Id: $TENANT" \
  -d '{"userName":"pilot.user","active":true,"emails":[{"value":"pilot@example.com","type":"work","primary":true}]}'

# Adapter swap demo
./tools/scripts/swap-demo.sh

# Audit write
curl -s -X POST localhost:8084/v1/audit/events \
  -H "Content-Type: application/json" -H "X-Tenant-Id: $TENANT" \
  -d '{"eventType":"pilot.smoke","message":"pilot ok","actor":"pilot"}'

# Approval
curl -s -X POST localhost:8086/v1/approvals \
  -H "Content-Type: application/json" -H "X-Tenant-Id: $TENANT" \
  -d '{"title":"Pilot approval","requestedBy":"pilot"}'
```

## Production gates

- Set `IDENTRA_JWT_REQUIRED=true` with issuer + JWKS
- Disable adapter dry-run and inject secrets via Key Vault
- Enable Temporal (`IDENTRA_TEMPORAL_ENABLED=true`)
- Enable Redis profile for identity cache
- Confirm Helm chart deploy to AKS staging
