# Identra Production

Hostname: **identra.idenaccess.com**

## Public URLs (after DNS)

| Page | URL |
|------|-----|
| Landing | https://identra.idenaccess.com/ |
| Ops dashboard | https://identra.idenaccess.com/ops |
| API | https://identra.idenaccess.com/v1/... |
| Health | https://identra.idenaccess.com/actuator/health |

## Cutover (already scripted)

```bash
chmod +x deploy/azure/prod-cutover.sh
./deploy/azure/prod-cutover.sh
```

The script installs ingress-nginx + cert-manager, switches portal/gateway to ClusterIP, applies prod Helm values, and prints the **ingress IP**.

Current ingress IP (as of cutover): **`4.255.15.72`**

```bash
# Pre-DNS smoke (HTTP)
curl -H 'Host: identra.idenaccess.com' http://4.255.15.72/
curl -H 'Host: identra.idenaccess.com' http://4.255.15.72/ops
curl -H 'Host: identra.idenaccess.com' http://4.255.15.72/actuator/health
```

## DNS (do this when ready)

```text
A  identra.idenaccess.com  ->  4.255.15.72
```

Then enable HTTPS redirect and wait for the cert:

```bash
helm upgrade identra deploy/helm/identra -n identra -f deploy/helm/identra/values-prod.yaml \
  --reuse-values --set ingress.sslRedirect=true
kubectl -n identra get certificate
```

TLS (Let's Encrypt) issues automatically once DNS resolves to the ingress IP.

## Entra ID JWT (enable when ready)

1. Create Entra app registration `Identra API` with Application ID URI `api://identra-api` (or set audience to the client ID).
2. In Helm prod values ([values-prod.yaml](../deploy/helm/identra/values-prod.yaml)):

```yaml
security:
  jwtRequired: "true"
  oidcIssuer: "https://login.microsoftonline.com/<tenant-id>/v2.0"
  oidcJwks: "https://login.microsoftonline.com/<tenant-id>/discovery/v2.0/keys"
  oidcAudience: "identra-api"
```

3. `helm upgrade` and send `Authorization: Bearer <token>` on API calls. Health endpoints stay open.

Current tenant ID on the Sponsorship subscription: `7a570fd1-8a60-4115-9e1b-68f9102f4eab` (pre-filled in values-prod).

## Key Vault

Terraform module `deploy/terraform/modules/keyvault` stores Postgres/Redis secrets. Sync to Kubernetes:

```bash
KV=$(cd deploy/terraform/environments/dev && terraform output -raw key_vault_name)
PG=$(az keyvault secret show --vault-name "$KV" -n postgres-password --query value -o tsv)
kubectl -n identra create secret generic identra-db \
  --from-literal=IDENTRA_DB_PASSWORD="$PG" \
  --from-literal=SPRING_DATASOURCE_PASSWORD="$PG" \
  --dry-run=client -o yaml | kubectl apply -f -
```

## Postgres firewall

Set `allow_public_postgres = false` in terraform.tfvars for production (keeps Azure services rule for AKS egress). Then `terraform apply`.
