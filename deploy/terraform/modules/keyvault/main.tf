variable "name" {
  type = string
}

variable "location" {
  type = string
}

variable "resource_group_name" {
  type = string
}

variable "tenant_id" {
  type = string
}

variable "postgres_password" {
  type      = string
  sensitive = true
}

variable "redis_primary_key" {
  type      = string
  sensitive = true
  default   = ""
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "this" {
  name                       = var.name
  location                   = var.location
  resource_group_name        = var.resource_group_name
  tenant_id                  = var.tenant_id
  sku_name                   = "standard"
  soft_delete_retention_days = 7
  purge_protection_enabled   = false
  rbac_authorization_enabled = true

  tags = {
    app     = "identra"
    managed = "terraform"
  }
}

resource "azurerm_role_assignment" "admin" {
  scope                = azurerm_key_vault.this.id
  role_definition_name = "Key Vault Administrator"
  principal_id         = data.azurerm_client_config.current.object_id
}

resource "azurerm_key_vault_secret" "postgres_password" {
  name         = "postgres-password"
  value        = var.postgres_password
  key_vault_id = azurerm_key_vault.this.id
  depends_on   = [azurerm_role_assignment.admin]
}

resource "azurerm_key_vault_secret" "redis_key" {
  count        = var.redis_primary_key != "" ? 1 : 0
  name         = "redis-primary-key"
  value        = var.redis_primary_key
  key_vault_id = azurerm_key_vault.this.id
  depends_on   = [azurerm_role_assignment.admin]
}

output "id" {
  value = azurerm_key_vault.this.id
}

output "name" {
  value = azurerm_key_vault.this.name
}

output "uri" {
  value = azurerm_key_vault.this.vault_uri
}
