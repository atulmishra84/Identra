terraform {
  required_version = ">= 1.6.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }
}

provider "azurerm" {
  features {}
}

variable "prefix" {
  type        = string
  description = "Name prefix for Identra resources"
  default     = "identra-dev"
}

variable "location" {
  type    = string
  default = "eastus"
}

resource "azurerm_resource_group" "identra" {
  name     = "${var.prefix}-rg"
  location = var.location
}

output "resource_group_name" {
  value = azurerm_resource_group.identra.name
}
