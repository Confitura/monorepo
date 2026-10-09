# Secret environment variable values, decrypted in process.
#
# SOPS reads the age identity from SOPS_AGE_KEY in the environment. The private
# key is a Paperclip secret and a GitHub Actions secret; only the public key is
# in this repo, in infra/.sops.yaml.
#
# Note for review: decrypted values land in OpenTofu state. The state bucket is
# the security boundary for them - keep B2 server-side encryption and versioning
# on, and keep the bucket private.
data "sops_file" "secrets" {
  source_file = "${path.module}/secrets.enc.yaml"
}

locals {
  # sops flattens nested YAML into dotted keys, so "webpage.FOO" is variable FOO
  # of the webpage application. Splitting per application keeps one encrypted
  # file for the environment instead of one per service.
  secrets_for = {
    for app_key in keys(var.applications) : app_key => {
      for k, v in data.sops_file.secrets.data :
      trimprefix(k, "${app_key}.") => v
      if startswith(k, "${app_key}.")
    }
  }
}

module "app" {
  source   = "../modules/coolify-app"
  for_each = var.applications

  app                  = each.value.app
  env_vars             = each.value.env_vars
  secret_env_vars      = local.secrets_for[each.key]
  secret_env_var_flags = each.value.secret_env_var_flags
}
