output "uuid" {
  description = "Coolify application UUID. Matches the uuid used by deploy-images.yml."
  value       = coolify_application_docker_image.this.uuid
}

output "status" {
  description = "Coolify's last observed application status, e.g. running."
  value       = coolify_application_docker_image.this.status
}

output "env_var_names" {
  description = "Every environment variable name this module manages, secret ones included. Names only."
  value       = sort(concat(keys(var.env_vars), nonsensitive(keys(var.secret_env_vars))))
}
