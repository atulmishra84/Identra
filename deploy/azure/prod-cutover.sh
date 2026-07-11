#!/usr/bin/env bash
# Production cutover: ingress + TLS + ClusterIP services for Identra.
# DNS for identra.idenaccess.com can be updated later to the printed ingress IP.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TF_DIR="${ROOT}/deploy/terraform/environments/dev"
NS="${IDENTRA_NAMESPACE:-identra}"
HOST="${IDENTRA_HOST:-identra.idenaccess.com}"

cd "$TF_DIR"
RG="$(terraform output -raw resource_group_name)"
AKS="$(terraform output -raw aks_cluster_name)"
ACR="$(terraform output -raw acr_login_server)"
PG_JDBC="$(terraform output -raw postgres_jdbc_url)"
PG_USER="$(terraform output -raw postgres_user)"
REDIS_HOST="$(terraform output -raw redis_hostname)"
REDIS_PORT="$(terraform output -raw redis_ssl_port)"

az aks get-credentials -g "$RG" -n "$AKS" --overwrite-existing

echo "==> ingress-nginx"
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx >/dev/null 2>&1 || true
helm repo update ingress-nginx >/dev/null
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-health-probe-request-path"=/healthz \
  --wait --timeout 10m

echo "==> cert-manager"
helm repo add jetstack https://charts.jetstack.io >/dev/null 2>&1 || true
helm repo update jetstack >/dev/null
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager --create-namespace \
  --set crds.enabled=true \
  --wait --timeout 10m
kubectl apply -f "${ROOT}/deploy/k8s/cert-manager/cluster-issuer.yaml"

echo "==> Promote images to :0.3.0 in ACR"
ACR_NAME="${ACR%%.*}"
SERVICES=(
  api-gateway identity-service provisioning-service tenant-service audit-service
  adapter-runtime approval-service ai-gateway governance-service connector-factory
  identity-core-service migration-service marketplace-service automation-factory
  automation-runtime certification-service portal-web
)
for s in "${SERVICES[@]}"; do
  if az acr repository show -n "$ACR_NAME" --image "${s}:0.1.0-SNAPSHOT" >/dev/null 2>&1; then
    az acr import -n "$ACR_NAME" \
      --source "${ACR}/${s}:0.1.0-SNAPSHOT" \
      --image "${s}:0.3.0" \
      --force >/dev/null
    echo "  ${s}:0.3.0"
  else
    echo "  skip ${s}"
  fi
done

echo "==> Helm upgrade with values-prod.yaml"
helm upgrade --install identra "${ROOT}/deploy/helm/identra" \
  --namespace "$NS" --create-namespace \
  -f "${ROOT}/deploy/helm/identra/values-prod.yaml" \
  --set "image.registry=${ACR}" \
  --set "ingress.host=${HOST}" \
  --set "postgresql.jdbcUrl=${PG_JDBC}" \
  --set "postgresql.username=${PG_USER}" \
  --set "redis.host=${REDIS_HOST}" \
  --set "redis.port=${REDIS_PORT}" \
  --set "services.api-gateway.serviceType=ClusterIP" \
  --set "portal.serviceType=ClusterIP"

# Ensure service types are ClusterIP even if helm ownership conflicts
kubectl -n "$NS" patch svc identra-api-gateway -p '{"spec":{"type":"ClusterIP"}}' --type merge || true
kubectl -n "$NS" patch svc identra-portal-web -p '{"spec":{"type":"ClusterIP"}}' --type merge || true

echo "==> Waiting for ingress IP"
INGRESS_IP=""
for i in $(seq 1 60); do
  INGRESS_IP="$(kubectl -n ingress-nginx get svc ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)"
  [[ -n "$INGRESS_IP" ]] && break
  sleep 5
done

echo ""
echo "=============================================="
echo " Production ingress ready"
echo "=============================================="
echo " Ingress IP:  ${INGRESS_IP:-pending}"
echo " Hostname:    ${HOST}"
echo ""
echo " When ready, create DNS:"
echo "   A  ${HOST}  ->  ${INGRESS_IP:-<ingress-ip>}"
echo ""
echo " Until DNS is updated, test via Host header:"
echo "   curl -H 'Host: ${HOST}' http://${INGRESS_IP}/"
echo "   curl -H 'Host: ${HOST}' http://${INGRESS_IP}/ops"
echo "   curl -H 'Host: ${HOST}' http://${INGRESS_IP}/actuator/health"
echo ""
echo " TLS certificate will issue after DNS points to this IP."
echo "=============================================="
