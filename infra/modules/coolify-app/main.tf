resource "coolify_application_docker_image" "this" {
  # Required
  docker_image  = var.app.docker_image
  ports_exposes = var.app.ports_exposes
  project_uuid  = var.app.project_uuid
  server_uuid   = var.app.server_uuid

  # Identity
  name             = var.app.name
  description      = var.app.description
  environment_name = var.app.environment_name

  # Image
  docker_registry_image_tag = var.app.docker_registry_image_tag
  git_commit_sha            = var.app.git_commit_sha

  # Routing
  domains                = var.app.domains
  noindex_domains        = var.app.noindex_domains
  redirect               = var.app.redirect
  force_domain_override  = var.app.force_domain_override
  is_force_https_enabled = var.app.is_force_https_enabled
  is_gzip_enabled        = var.app.is_gzip_enabled
  is_stripprefix_enabled = var.app.is_stripprefix_enabled
  ports_mappings         = var.app.ports_mappings

  # Container
  custom_docker_run_options  = var.app.custom_docker_run_options
  custom_labels              = var.app.custom_labels
  custom_network_aliases     = var.app.custom_network_aliases
  custom_nginx_configuration = var.app.custom_nginx_configuration
  custom_internal_name       = var.app.custom_internal_name
  connect_to_docker_network  = var.app.connect_to_docker_network
  destination_uuid           = var.app.destination_uuid
  docker_images_to_keep      = var.app.docker_images_to_keep
  max_restart_count          = var.app.max_restart_count
  stop_grace_period          = var.app.stop_grace_period

  # Resource limits
  limits_cpu_shares         = var.app.limits_cpu_shares
  limits_cpus               = var.app.limits_cpus
  limits_cpuset             = var.app.limits_cpuset
  limits_memory             = var.app.limits_memory
  limits_memory_reservation = var.app.limits_memory_reservation
  limits_memory_swap        = var.app.limits_memory_swap
  limits_memory_swappiness  = var.app.limits_memory_swappiness

  # Health checks
  health_check_enabled       = var.app.health_check_enabled
  health_check_type          = var.app.health_check_type
  health_check_scheme        = var.app.health_check_scheme
  health_check_host          = var.app.health_check_host
  health_check_port          = var.app.health_check_port
  health_check_path          = var.app.health_check_path
  health_check_method        = var.app.health_check_method
  health_check_command       = var.app.health_check_command
  health_check_response_text = var.app.health_check_response_text
  health_check_return_code   = var.app.health_check_return_code
  health_check_interval      = var.app.health_check_interval
  health_check_timeout       = var.app.health_check_timeout
  health_check_retries       = var.app.health_check_retries
  health_check_start_period  = var.app.health_check_start_period

  # Lifecycle commands
  pre_deployment_command            = var.app.pre_deployment_command
  pre_deployment_command_container  = var.app.pre_deployment_command_container
  post_deployment_command           = var.app.post_deployment_command
  post_deployment_command_container = var.app.post_deployment_command_container
  start_command                     = var.app.start_command

  # Static / SPA serving
  is_spa            = var.app.is_spa
  is_static         = var.app.is_static
  static_image      = var.app.static_image
  publish_directory = var.app.publish_directory
  base_directory    = var.app.base_directory

  # Deploy behaviour
  is_auto_deploy_enabled = var.app.is_auto_deploy_enabled
  instant_deploy         = var.app.instant_deploy
  redeploy_on_update     = var.app.redeploy_on_update
  watch_paths            = var.app.watch_paths
  use_build_server       = var.app.use_build_server

  lifecycle {
    # These are live conference services. A plan that destroys or replaces one
    # must fail at plan time, not be caught by a human reading the output.
    # Removing this guard is a reviewed change, not a workaround for a diff.
    prevent_destroy = true
  }
}

# One Coolify variable per resource, rather than coolify_envs_bulk.
#
# coolify_envs_bulk takes a single map and overwrites every variable not present
# in it on the next apply, and its whole map is marked sensitive, so no env var
# change would ever be readable in a plan. Per-variable resources give one state
# address per variable, keep non-secret values reviewable, and are the only form
# that can express the is_build / is_runtime / is_preview flags Coolify stores.

resource "coolify_environment_variable" "plain" {
  for_each = var.env_vars

  application_uuid = coolify_application_docker_image.this.uuid
  key              = each.key
  value            = each.value.value

  is_build      = each.value.is_build
  is_runtime    = each.value.is_runtime
  is_preview    = each.value.is_preview
  is_literal    = each.value.is_literal
  is_multiline  = each.value.is_multiline
  is_shown_once = each.value.is_shown_once
  comment       = each.value.comment
}

resource "coolify_environment_variable" "secret" {
  # Values are sensitive; names are not. for_each cannot take a sensitive
  # collection, so unmark the key set only and look each value up by name.
  for_each = nonsensitive(toset(keys(var.secret_env_vars)))

  application_uuid = coolify_application_docker_image.this.uuid
  key              = each.value
  value            = var.secret_env_vars[each.value]

  is_build      = try(var.secret_env_var_flags[each.value].is_build, null)
  is_runtime    = try(var.secret_env_var_flags[each.value].is_runtime, null)
  is_preview    = try(var.secret_env_var_flags[each.value].is_preview, null)
  is_literal    = try(var.secret_env_var_flags[each.value].is_literal, null)
  is_multiline  = try(var.secret_env_var_flags[each.value].is_multiline, null)
  is_shown_once = try(var.secret_env_var_flags[each.value].is_shown_once, null)
  comment       = try(var.secret_env_var_flags[each.value].comment, null)
}
