#!/usr/bin/env bash
# Provision Azure infra (Terraform) and deploy Identra V1–V3 (Helm).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TF_DIR="${ROOT}/deploy/terraform/environments/dev"
HELM_DIR="${ROOT}/deploy/helm/identra"
NAMESPACE="${IDENTRA_NAMESPACE:-identra}"
RELEASE="${IDENTRA_RELEASE:-identra}"
TAG="${IDENTRA_IMAGE_TAG:-0.1.0-SNAPSHOT}"

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home}"
export PATH="$JAVA_HOME/bin:$PATH"

cd "$TF_DIR"

if [[ ! -f terraform.tfvars ]]; then
  cp example.tfvars terraform.tfvars
  echo "Created terraform.tfvars from example.tfvars"
fi

echo "==> Terraform init/apply"
terraform init -upgrade
terraform apply -auto-approve

RG="$(terraform output -raw resource_group_name)"
AKS="$(terraform output -raw aks_cluster_name)"
ACR_SERVER="$(terraform output -raw acr_login_server)"
ACR_NAME="$(terraform output -raw acr_name)"
PG_JDBC="$(terraform output -raw postgres_jdbc_url)"
PG_USER="$(terraform output -raw postgres_user)"
PG_PASS="$(terraform output -raw postgres_password)"
REDIS_HOST="$(terraform output -raw redis_hostname)"
REDIS_PORT="$(terraform output -raw redis_ssl_port)"
REDIS_KEY="$(terraform output -raw redis_primary_key)"

echo "==> Build & push images to ${ACR_SERVER}"
"${ROOT}/deploy/azure/build-and-push.sh" "${ACR_SERVER}" "${TAG}"

echo "==> kubectl context ${AKS}"
az aks get-credentials --resource-group "${RG}" --name "${AKS}" --overwrite-existing

echo "==> Create namespace + secrets"
kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -
kubectl -n "${NAMESPACE}" create secret generic identra-db \
  --from-literal=IDENTRA_DB_PASSWORD="${PG_PASS}" \
  --from-literal=SPRING_DATASOURCE_PASSWORD="${PG_PASS}" \
  --dry-run=client -o yaml | kubectl apply -f -

if [[ -n "${REDIS_HOST}" ]]; then
  kubectl -n "${NAMESPACE}" create secret generic identra-redis \
    --from-literal=IDENTRA_REDIS_PASSWORD="${REDIS_KEY}" \
    --from-literal=SPRING_DATA_REDIS_PASSWORD="${REDIS_KEY}" \
    --dry-run=client -o yaml | kubectl apply -f -
fi

echo "==> Helm upgrade --install ${RELEASE}"
helm upgrade --install "${RELEASE}" "${HELM_DIR}" \
  --namespace "${NAMESPACE}" \
  --create-namespace \
  --set "image.registry=${ACR_SERVER}" \
  --set "image.tag=${TAG}" \
  --set "postgresql.jdbcUrl=${PG_JDBC}" \
  --set "postgresql.username=${PG_USER}" \
  --set "redis.host=${REDIS_HOST}" \
  --set "redis.port=${REDIS_PORT}" \
  --wait \
  --timeout 15m

echo "==> Waiting for gateway external IP"
for i in $(seq 1 60); do
  IP="$(kubectl -n "${NAMESPACE}" get svc "${RELEASE}-api-gateway" -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)"
  if [[ -n "${IP}" ]]; then
    echo ""
    echo "Identra V1–V3 is live on Azure"
    echo "  Gateway:  http://${IP}:8080/health"
    echo "  Resource group: ${RG}"
    echo "  AKS: ${AKS}"
    echo "  ACR: ${ACR_NAME}"
    exit 0
  fi
  sleep 10
done

echo "Deployed, but LoadBalancer IP not ready yet. Check:"
echo "  kubectl -n ${NAMESPACE} get svc ${RELEASE}-api-gateway"
