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

  type = map(object({
    # Mirrors the Coolify application. See modules/coolify-app/variables.tf for
    # why each attribute is listed there rather than defaulted.
    app = any

    # Non-secret environment variables: name => { value, flags... }
    env_vars = optional(map(object({
      value         = string
      is_build      = optional(bool)
      is_runtime    = optional(bool)
      is_preview    = optional(bool)
      is_literal    = optional(bool)
      is_multiline  = optional(bool)
      is_shown_once = optional(bool)
      comment       = optional(string)
    })), {})

    # Flags for the secret variables, whose values live in secrets.enc.yaml.
    # Flags are not secret and belong in the readable file.
    secret_env_var_flags = optional(map(object({
      is_build      = optional(bool)
      is_runtime    = optional(bool)
      is_preview    = optional(bool)
      is_literal    = optional(bool)
      is_multiline  = optional(bool)
      is_shown_once = optional(bool)
      comment       = optional(string)
    })), {})
  }))

  default = {}
}
