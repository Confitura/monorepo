variable "coolify_endpoint" {
  description = "Coolify API endpoint. Not a secret."
  type        = string
  default     = "https://admin.confitura.pl"
}

variable "applications" {
  description = <<-EOT
    The Confitura applications under management, keyed by a short stable name.
    The key is part of every state address and of the SOPS key prefix, so
    renaming one is a state move, not an edit.

    Filled in from the live Coolify configuration in
    production.auto.tfvars. That file is the one an owner edits to change a
    non-secret environment variable.
  EOT

  # Deliberately `any`, not map(object({ app = any, ... })). A map's elements
  # must share one type, and the applications' `app` objects differ (only the
  # backend sets noindex_domains), so a typed map rejects the whole file with
  # "cannot find a common base type". modules/coolify-app types app, env_vars
  # and secret_env_var_flags strictly, so nothing goes unchecked.
  #
  # Each entry: { app = {...}, env_vars = optional, secret_env_var_flags = optional }
  type = any

  default = {}
}
