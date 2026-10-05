terraform {
  required_version = ">= 1.6.0"

  required_providers {
    coolify = {
      source  = "coolify-terraform/coolify"
      version = "~> 0.1.25"
    }
  }
}
