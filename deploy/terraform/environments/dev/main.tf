terraform {
  required_version = ">= 1.6.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

variable "prefix" {
  type        = string
  description = "Name prefix for Identra resources"
  default     = "identra"
}

variable "location" {
  type    = string
  default = "eastus"
}

variable "node_count" {
  type    = number
  default = 2
}

variable "node_vm_size" {
  type    = string
  default = "Standard_B2s"
}

variable "deploy_redis" {
  type    = bool
  default = true
}

resource "random_string" "suffix" {
  length  = 6
  upper   = false
  special = false
}

resource "random_password" "postgres" {
  length           = 24
  special          = true
  override_special = "!#-_"
}

resource "azurerm_resource_group" "identra" {
  name     = "${var.prefix}-v123-rg"
  location = var.location

  tags = {
    app     = "identra"
    stack   = "v1-v3"
    managed = "terraform"
  }
}

module "acr" {
  source              = "../../modules/acr"
  name                = "${replace(var.prefix, "-", "")}${random_string.suffix.result}"
  location            = azurerm_resource_group.identra.location
  resource_group_name = azurerm_resource_group.identra.name
}

module "aks" {
  source              = "../../modules/aks"
  name                = "${var.prefix}-aks"
  location            = azurerm_resource_group.identra.location
  resource_group_name = azurerm_resource_group.identra.name
  dns_prefix          = "${var.prefix}-aks"
  node_count          = var.node_count
  node_vm_size        = var.node_vm_size
  acr_id              = module.acr.id
}

module "postgres" {
  source                 = "../../modules/postgres"
  name                   = "${var.prefix}-pg-${random_string.suffix.result}"
  location               = azurerm_resource_group.identra.location
  resource_group_name    = azurerm_resource_group.identra.name
  administrator_login    = "identra"
  administrator_password = random_password.postgres.result
}

module "redis" {
  count               = var.deploy_redis ? 1 : 0
  source              = "../../modules/redis"
  name                = "${var.prefix}-redis-${random_string.suffix.result}"
  location            = azurerm_resource_group.identra.location
  resource_group_name = azurerm_resource_group.identra.name
}

output "resource_group_name" {
  value = azurerm_resource_group.identra.name
}

output "acr_name" {
  value = module.acr.name
}

output "acr_login_server" {
  value = module.acr.login_server
}

output "acr_admin_username" {
  value = module.acr.admin_username
}

output "acr_admin_password" {
  value     = module.acr.admin_password
  sensitive = true
}

output "aks_cluster_name" {
  value = module.aks.cluster_name
}

output "postgres_fqdn" {
  value = module.postgres.fqdn
}

output "postgres_jdbc_url" {
  value = module.postgres.jdbc_url
}

output "postgres_user" {
  value = module.postgres.administrator_login
}

output "postgres_password" {
  value     = random_password.postgres.result
  sensitive = true
}

output "redis_hostname" {
  value = var.deploy_redis ? module.redis[0].hostname : ""
}

output "redis_ssl_port" {
  value = var.deploy_redis ? module.redis[0].ssl_port : 0
}

output "redis_primary_key" {
  value     = var.deploy_redis ? module.redis[0].primary_access_key : ""
  sensitive = true
}
