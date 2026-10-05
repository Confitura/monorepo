provider "coolify" {
  endpoint = var.coolify_endpoint

  # token comes from COOLIFY_TOKEN in the environment. Never set it here.
  #
  # The token needs read:sensitive (or root). With a default-permission token
  # Coolify returns empty strings for sensitive fields while the real config
  # holds values, so every secret environment variable shows a diff on every
  # plan and the plan never converges.
  #
  # admin.confitura.pl is not behind Cloudflare Access (verified 2026-10-05:
  # GET /api/v1/version answers 401 from Coolify itself, no cf-ray, no Access
  # redirect), so cf_access_client_id / cf_access_client_secret stay unset.
}

provider "sops" {}
