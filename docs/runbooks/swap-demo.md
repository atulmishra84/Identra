# Okta ↔ SailPoint ISC Swap Demo

## Goal

Prove Identra’s primary business value: **changing IAM vendors does not require application rewrites**.

## Prerequisites

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
./gradlew :services:adapter-runtime:bootRun
# optional gateway
./gradlew :services:api-gateway:bootRun
```

Adapters default to **dry-run** mode (`IDENTRA_OKTA_DRY_RUN=true`, `IDENTRA_ISC_DRY_RUN=true`).

## Run

```bash
chmod +x tools/scripts/swap-demo.sh
./tools/scripts/swap-demo.sh
```

## What it shows

1. Lists registered adapters (`okta`, `sailpoint-isc`)
2. Dispatches the **same canonical identity payload** to Okta
3. Dispatches the **same payload** to SailPoint ISC

Only the adapter path changes (`/v1/adapters/{adapterId}/dispatch`). Downstream apps continue calling Identra `/v1/*` and `/scim/v2/*`.

## Production cutover

1. Set dry-run to false and supply Okta/ISC credentials via env
2. Update tenant adapter binding (applicationKey / default-adapter-id)
3. Re-run reconciliation / sync jobs
4. Keep both adapters attached during phased coexistence if needed
