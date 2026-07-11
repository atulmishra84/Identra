# Identra Terraform (Azure-first)

Modules and environments for AKS, PostgreSQL, Redis, and supporting resources.

```text
environments/
  dev/
  staging/
  prod/
modules/
  aks/
  postgres/
```

Usage (dev):

```bash
cd environments/dev
terraform init
terraform plan -var-file=example.tfvars
```

Do not commit real `*.tfvars` with secrets.
