# Deploy Identra V1–V3 on Azure

This stack provisions **all** Identra services from MVP/V1 through V3 onto a single Azure environment.

## What gets created

| Resource | Purpose |
|----------|---------|
| Resource group `identra-v123-rg` | All Identra cloud resources |
| Azure Container Registry | Service images |
| AKS (2× Standard_D2s_v7 in eastus) | Kubernetes runtime |
| PostgreSQL Flexible Server (B1ms in centralus) | Identity + tenant DBs (centralus avoids Sponsorship eastus offer restriction) |
| Azure Cache for Redis (Basic C0) | Optional identity cache |
| Helm release `identra` | 16 microservices (V1–V3) |

### Services by track

- **V1 / MVP:** api-gateway, identity, provisioning, tenant, audit, adapter-runtime, approval
- **V2:** ai-gateway, governance, connector-factory, identity-core, migration, marketplace
- **V3:** automation-factory, automation-runtime, certification

Gateway is exposed as a public **LoadBalancer** on port 8080.

## Prerequisites

- Azure CLI logged in (`az account show`)
- Terraform ≥ 1.6, Helm 3, Docker, JDK 21
- Enough quota in `eastus` for 2× B2s + Postgres + Redis

## One-command deploy

```bash
chmod +x deploy/azure/*.sh
./deploy/azure/deploy.sh
```

This runs Terraform apply → Gradle bootJar → Docker push to ACR → Helm install.

## Step-by-step

```bash
# 1. Infra
cd deploy/terraform/environments/dev
cp example.tfvars terraform.tfvars
terraform init
terraform apply

# 2. Images
ACR=$(terraform output -raw acr_login_server)
../../azure/build-and-push.sh "$ACR"

# 3. App
az aks get-credentials -g $(terraform output -raw resource_group_name) -n $(terraform output -raw aks_cluster_name)
# then run the helm portion of deploy.sh, or re-run ./deploy/azure/deploy.sh
```

## Verify

```bash
kubectl -n identra get pods
kubectl -n identra get svc identra-api-gateway
curl http://<EXTERNAL-IP>:8080/health
```

## Tear down

```bash
cd deploy/terraform/environments/dev
terraform destroy -auto-approve
```

## Cost notes

Dev sizing (D2s_v7 nodes + B1ms Postgres in centralus + Basic Redis) fits Sponsorship quotas. `eastus` blocks Postgres Flexible Server (`LocationIsOfferRestricted`); AKS rejects `Standard_B2s` — use `Standard_D2s_v7`. Build images with `--platform linux/amd64`.
