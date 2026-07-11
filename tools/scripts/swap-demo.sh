#!/usr/bin/env bash
# Identra MVP swap demo: provision via Okta adapter, then re-bind to SailPoint ISC without changing the client payload shape.
set -euo pipefail

TENANT="${IDENTRA_TENANT_ID:-11111111-1111-1111-1111-111111111111}"
GATEWAY="${IDENTRA_GATEWAY_URL:-http://localhost:8080}"
ADAPTERS="${IDENTRA_ADAPTER_RUNTIME_URL:-http://localhost:8085}"

echo "==> Adapter catalog"
curl -sS -H "X-Tenant-Id: $TENANT" "$GATEWAY/v1/adapters" | tee /tmp/identra-adapters.json
echo

echo "==> Dry-run CREATE on Okta adapter"
curl -sS -X POST "$ADAPTERS/v1/adapters/okta/dispatch" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: $TENANT" \
  -d "{
    \"tenantId\": \"$TENANT\",
    \"adapterId\": \"okta\",
    \"operation\": \"CREATE\",
    \"identity\": {
      \"id\": \"22222222-2222-2222-2222-222222222222\",
      \"tenantId\": \"$TENANT\",
      \"userName\": \"swap.demo\",
      \"active\": true,
      \"emails\": [{\"value\": \"swap.demo@example.com\", \"type\": \"work\", \"primary\": true}],
      \"name\": {\"formatted\": \"Swap Demo\", \"familyName\": \"Demo\", \"givenName\": \"Swap\"},
      \"employmentStatus\": \"ACTIVE\",
      \"sourceSystem\": \"identra\",
      \"version\": 0,
      \"etag\": \"W/\\\"demo\\\"\",
      \"createdAt\": \"2026-01-01T00:00:00Z\",
      \"updatedAt\": \"2026-01-01T00:00:00Z\"
    }
  }" | tee /tmp/identra-okta-create.json
echo

echo "==> Same canonical payload on SailPoint ISC adapter (swap)"
curl -sS -X POST "$ADAPTERS/v1/adapters/sailpoint-isc/dispatch" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: $TENANT" \
  -d "{
    \"tenantId\": \"$TENANT\",
    \"adapterId\": \"sailpoint-isc\",
    \"operation\": \"CREATE\",
    \"identity\": {
      \"id\": \"22222222-2222-2222-2222-222222222222\",
      \"tenantId\": \"$TENANT\",
      \"userName\": \"swap.demo\",
      \"active\": true,
      \"emails\": [{\"value\": \"swap.demo@example.com\", \"type\": \"work\", \"primary\": true}],
      \"name\": {\"formatted\": \"Swap Demo\", \"familyName\": \"Demo\", \"givenName\": \"Swap\"},
      \"employmentStatus\": \"ACTIVE\",
      \"sourceSystem\": \"identra\",
      \"version\": 0,
      \"etag\": \"W/\\\"demo\\\"\",
      \"createdAt\": \"2026-01-01T00:00:00Z\",
      \"updatedAt\": \"2026-01-01T00:00:00Z\"
    }
  }" | tee /tmp/identra-isc-create.json
echo

echo "==> Swap demo complete. Application clients keep using Identra Universal API; only adapter binding changes."
