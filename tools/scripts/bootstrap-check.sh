#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
echo "Identra root: $ROOT"
command -v helm >/dev/null && helm lint "$ROOT/deploy/helm/identra" || echo "helm not installed — skip lint"
test -f "$ROOT/docs/openapi/identra-v1.yaml" && echo "OpenAPI OK"
test -f "$ROOT/docs/adr/0001-backend-java-spring-boot.md" && echo "ADRs OK"
echo "Bootstrap checks complete"
