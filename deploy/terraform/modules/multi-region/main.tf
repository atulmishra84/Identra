# Multi-region active-active foundation (v3)
# Pair with Front Door / Traffic Manager and regional AKS + Postgres replicas.

variable "primary_region" {
  type    = string
  default = "eastus2"
}

variable "secondary_region" {
  type    = string
  default = "westus2"
}

variable "name_prefix" {
  type    = string
  default = "identra"
}

output "active_active_notes" {
  value = <<-EOT
  Deploy identical Helm releases in ${var.primary_region} and ${var.secondary_region}.
  Use global ingress with health probes on /health.
  Kafka MirrorMaker2 + Postgres logical replication for identity/audit streams.
  Sticky tenant routing optional via X-Tenant-Id hash.
  EOT
}
