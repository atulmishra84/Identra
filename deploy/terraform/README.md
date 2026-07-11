# Identra Terraform (Azure-first)

Full V1–V3 platform deploy lives under `environments/dev` plus `deploy/azure/deploy.sh`.

```text
environments/
  dev/          # RG + ACR + AKS + Postgres + Redis
modules/
  aks/
  acr/
  postgres/
  redis/
  multi-region/
```

See [docs/AZURE.md](../../docs/AZURE.md) for the end-to-end guide.

```bash
cd environments/dev
cp example.tfvars terraform.tfvars
terraform init
terraform plan
terraform apply
```

Do not commit real `*.tfvars` / state secrets.
