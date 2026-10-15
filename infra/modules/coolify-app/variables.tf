# The shape of a Confitura Coolify application.
#
# All three services deploy a pre-built image from GHCR, so this module wraps
# coolify_application_docker_image plus the application's environment variables.
#
# Attribute selection is not arbitrary. The provider schema (v0.1.25) splits
# application attributes into two classes, and only one of them is safe to omit:
#
#   optional + computed -> omitting it keeps whatever Coolify holds. Safe.
#   optional only       -> omitting it means "null", which after an import of a
#                          live value shows up as a diff that removes the value.
#
# Every "optional only" attribute is therefore exposed below, so an imported
# value can be mirrored in tfvars and the plan can come out empty. Read
# docs/infra-notes before adding one: `tofu providers schema -json` is the
# authority, not this comment.

variable "app" {
  description = "The live Coolify application, mirrored from the Coolify API."

  type = object({
    # ---- Required by the provider -------------------------------------
    # Coolify stores the image without its tag; the tag lives in
    # docker_registry_image_tag. Mirror both as the API returns them.
    docker_image  = string
    ports_exposes = string
    project_uuid  = string
    server_uuid   = string

    # ---- Identity -----------------------------------------------------
    name             = optional(string)
    description      = optional(string)
    environment_name = optional(string) # import defaults this to "production"

    # ---- Image --------------------------------------------------------
    docker_registry_image_tag = optional(string)
    git_commit_sha            = optional(string)

    # ---- Routing ------------------------------------------------------
    domains                = optional(string) # comma-separated full URLs
    noindex_domains        = optional(list(string))
    redirect               = optional(string)
    force_domain_override  = optional(bool)
    is_force_https_enabled = optional(bool)
    is_gzip_enabled        = optional(bool)
    is_stripprefix_enabled = optional(bool)
    ports_mappings         = optional(string)

    # ---- Container ----------------------------------------------------
    custom_docker_run_options  = optional(string)
    custom_labels              = optional(string)
    custom_network_aliases     = optional(string)
    custom_nginx_configuration = optional(string)
    custom_internal_name       = optional(string)
    connect_to_docker_network  = optional(bool)
    destination_uuid           = optional(string)
    docker_images_to_keep      = optional(number)
    max_restart_count          = optional(number)
    stop_grace_period          = optional(number)

    # ---- Resource limits (all optional-only: mirror or lose them) -----
    limits_cpu_shares         = optional(string)
    limits_cpus               = optional(string)
    limits_cpuset             = optional(string)
    limits_memory             = optional(string)
    limits_memory_reservation = optional(string)
    limits_memory_swap        = optional(string)
    limits_memory_swappiness  = optional(string)

    # ---- Health checks ------------------------------------------------
    health_check_enabled       = optional(bool)
    health_check_type          = optional(string)
    health_check_scheme        = optional(string)
    health_check_host          = optional(string)
    health_check_port          = optional(string)
    health_check_path          = optional(string)
    health_check_method        = optional(string)
    health_check_command       = optional(string)
    health_check_response_text = optional(string)
    health_check_return_code   = optional(number)
    health_check_interval      = optional(number)
    health_check_timeout       = optional(number)
    health_check_retries       = optional(number)
    health_check_start_period  = optional(number)

    # ---- Lifecycle commands -------------------------------------------
    pre_deployment_command            = optional(string)
    pre_deployment_command_container  = optional(string)
    post_deployment_command           = optional(string)
    post_deployment_command_container = optional(string)
    start_command                     = optional(string)

    # ---- Static / SPA serving -----------------------------------------
    is_spa            = optional(bool)
    is_static         = optional(bool)
    static_image      = optional(string)
    publish_directory = optional(string)
    base_directory    = optional(string)

    # ---- Deploy behaviour ---------------------------------------------
    # instant_deploy and redeploy_on_update are provider behaviour flags, not
    # Coolify fields. Leave them unset unless Reviewer agrees to a value: a
    # true redeploy_on_update restarts a live conference service on any config
    # change, and setting either one can produce a one-time post-import diff.
    is_auto_deploy_enabled = optional(bool)
    instant_deploy         = optional(bool)
    redeploy_on_update     = optional(bool)
    watch_paths            = optional(string)
    use_build_server       = optional(bool)
  })
}

variable "env_vars" {
  description = <<-EOT
    Non-secret environment variables, one Coolify variable per entry. Keyed by
    variable name. Values are visible in `tofu plan` on purpose: the point of
    this repo is that a reviewer can read a config change.

    All flag fields are optional+computed in the provider, so leaving one out
    keeps Coolify's current setting instead of clearing it.
  EOT

  type = map(object({
    value         = string
    is_build      = optional(bool)
    is_runtime    = optional(bool)
    is_preview    = optional(bool)
    is_literal    = optional(bool)
    is_multiline  = optional(bool)
    is_shown_once = optional(bool)
    comment       = optional(string)
  }))

  default = {}
}

variable "secret_env_vars" {
  description = <<-EOT
    Secret environment variables as name => value, supplied from the
    SOPS-encrypted file by the calling root. Values never appear in a plan.

    Variable names are not secret and are intentionally readable: the module
    unmarks the map's keys (not its values) so they can drive for_each.
  EOT

  type      = map(string)
  sensitive = true
  default   = {}
}

variable "secret_env_var_flags" {
  description = <<-EOT
    Optional per-variable flags for secret variables, keyed by the same name.
    Kept separate from the values so flags stay readable in a plan: a flag is
    not a secret, and a diff on one should be reviewable.
  EOT

  type = map(object({
    is_build      = optional(bool)
    is_runtime    = optional(bool)
    is_preview    = optional(bool)
    is_literal    = optional(bool)
    is_multiline  = optional(bool)
    is_shown_once = optional(bool)
    comment       = optional(string)
  }))

  default = {}
}
