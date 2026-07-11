#!/usr/bin/env bash
# Build and push all Identra V1–V3 service images to Azure Container Registry.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT"

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home}"
export PATH="$JAVA_HOME/bin:$PATH"

ACR_LOGIN_SERVER="${1:?Usage: $0 <acrLoginServer> [tag]}"
TAG="${2:-0.1.0-SNAPSHOT}"

SERVICES=(
  api-gateway
  identity-service
  provisioning-service
  tenant-service
  audit-service
  adapter-runtime
  approval-service
  ai-gateway
  governance-service
  connector-factory
  identity-core-service
  migration-service
  marketplace-service
  automation-factory
  automation-runtime
  certification-service
)

echo "==> Building bootJars"
MODULES=()
for s in "${SERVICES[@]}"; do
  MODULES+=(":services:${s}:bootJar")
done
./gradlew "${MODULES[@]}" --no-daemon -x test

echo "==> Docker login to ${ACR_LOGIN_SERVER}"
az acr login --name "${ACR_LOGIN_SERVER%%.*}"

echo "==> Building and pushing images (tag=${TAG})"
for s in "${SERVICES[@]}"; do
  jar="services/${s}/build/libs/${s}-0.1.0-SNAPSHOT.jar"
  if [[ ! -f "$jar" ]]; then
    # Spring Boot may produce a plain jar + boot jar; prefer the executable boot jar
    jar="$(ls -1 "services/${s}/build/libs/"*.jar | grep -v plain | head -1)"
  fi
  image="${ACR_LOGIN_SERVER}/${s}:${TAG}"
  echo "---- ${image} (${jar})"
  docker build \
    --platform linux/amd64 \
    -f deploy/docker/Dockerfile.runtime \
    --build-arg "JAR_FILE=${jar}" \
    -t "${image}" \
    .
  docker push "${image}"
done

echo "==> Done. Pushed ${#SERVICES[@]} images to ${ACR_LOGIN_SERVER}"
