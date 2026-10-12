# Confitura production, non-secret configuration.
#
# This is the file you edit to change a non-secret environment variable. Secret
# values go in secrets.enc.yaml instead; see infra/README.md.
#
# STATUS: not yet populated. The three applications are deliberately absent
# until the Coolify inventory has been read with a read:sensitive token, so a
# "No changes" plan from this file proves nothing yet. The empty-plan proof on
# BCC-2 requires all three present and imported.
#
# Known from the repo, still to be confirmed against the Coolify API:
#
#   key        | Coolify app UUID           | image (tag stripped by Coolify)
#   -----------+----------------------------+-----------------------------------------------
#   webpage    | jjbrvlzsq0ck1oqx46b0i0v6   | ghcr.io/confitura/confitura-webpage
#   admin_app  | a4z9gvkuf4vk784dxd39nj2f   | ghcr.io/confitura/confitura-admin-app
#   backend    | wqwwmtfjn4xuhsgj4uzwfd4x   | ghcr.io/confitura/confitura-backend
#
# UUIDs are the deploy?uuid= targets in .github/workflows/deploy-images.yml.
# project_uuid and server_uuid are not in the repo and not returned by Coolify's
# application GET - they come from the inventory read and are required.
#
# admin_app's VITE_API_URL and VITE_SELF_URL are baked in at image build time as
# docker --build-arg in deploy-images.yml. They are not Coolify environment
# variables and must not be added here; changing them is a code change.
#
# Template for one application, to be filled from the inventory:
#
# applications = {
#   webpage = {
#     app = {
#       docker_image              = "ghcr.io/confitura/confitura-webpage"
#       docker_registry_image_tag = "latest"
#       ports_exposes             = "80"
#       project_uuid              = "<from inventory>"
#       server_uuid               = "<from inventory>"
#       environment_name          = "production"
#       name                      = "<from inventory>"
#       domains                   = "https://confitura.pl"
#       # plus every attribute Coolify actually holds a value for - the
#       # optional-only ones in modules/coolify-app/variables.tf are the ones
#       # that produce a diff if they are left out.
#     }
#     env_vars = {
#       # EXAMPLE_FLAG = { value = "true", is_runtime = true }
#     }
#     secret_env_var_flags = {}
#   }
# }

applications = {}
