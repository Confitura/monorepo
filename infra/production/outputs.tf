output "application_uuids" {
  description = "Managed Coolify application UUIDs, keyed by application name."
  value       = { for k, m in module.app : k => m.uuid }
}

output "managed_env_var_names" {
  description = "Environment variable names under management, keyed by application. Names only, no values."
  value       = { for k, m in module.app : k => m.env_var_names }
}
