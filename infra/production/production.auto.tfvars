# Confitura production, non-secret configuration.
#
# This is the file you edit to change a non-secret environment variable. Secret
# values go in secrets.enc.yaml instead; see infra/README.md.
#
# Populated 2026-10-09 from the live Coolify 4.4.3 configuration
# (scripts/coolify-inventory.sh) and the provider's own read of each
# application (tofu import config generation). The goal is an empty plan.
#
# Not managed here, deliberately:
#   - Preview-deployment copies of the environment variables (is_preview).
#     Preview deployments are disabled on all three applications.
#   - admin_app's VITE_API_URL / VITE_SELF_URL: they exist in Coolify but do
#     nothing, because the image is built in deploy-images.yml with those
#     values as --build-arg.
#
# Optional-only module attributes are mirrored from the live values; omitting
# one would plan a change that clears it. See modules/coolify-app/variables.tf.

applications = {
  webpage = {
    app = {
      connect_to_docker_network = false
      docker_image              = "ghcr.io/confitura/confitura-webpage"
      docker_images_to_keep     = 2
      domains                   = "https://2026.confitura.pl,https://confitura.pl"
      environment_name          = "production"
      health_check_enabled      = false
      health_check_host         = "localhost"
      health_check_interval     = 5
      health_check_method       = "GET"
      health_check_path         = "/"
      health_check_retries      = 10
      health_check_return_code  = 200
      health_check_scheme       = "http"
      health_check_start_period = 5
      health_check_timeout      = 5
      health_check_type         = "http"
      instant_deploy            = false
      is_auto_deploy_enabled    = true
      is_force_https_enabled    = true
      is_gzip_enabled           = true
      is_spa                    = false
      is_static                 = false
      is_stripprefix_enabled    = true
      max_restart_count         = 0
      name                      = "2026.confitura.pl"
      ports_exposes             = "80"
      project_uuid              = "r4w0wgc04o0swokooc488owg"
      redeploy_on_update        = false
      redirect                  = "both"
      server_uuid               = "yw0ksg404c4kw8cggskw084k"
      static_image              = "nginx:alpine"
      use_build_server          = false
    }

    env_vars = {
      "NUXT_PUBLIC_API_SERVER"     = { value = "https://resources.confitura.pl/edition-2026/" }
      "NUXT_PUBLIC_ARCHIVE_SERVER" = { value = "https://resources.confitura.pl/edition-2026/" }
      "NUXT_PUBLIC_CHAT_ENABLED"   = { value = "true" }
      "NUXT_PUBLIC_FILE_SERVER"    = { value = "https://resources.confitura.pl/edition-2026" }
    }
  }

  admin_app = {
    app = {
      connect_to_docker_network = false
      docker_image              = "ghcr.io/confitura/confitura-admin-app"
      docker_images_to_keep     = 2
      domains                   = "https://app.confitura.pl"
      environment_name          = "production"
      health_check_enabled      = false
      health_check_host         = "localhost"
      health_check_interval     = 5
      health_check_method       = "GET"
      health_check_path         = "/"
      health_check_retries      = 10
      health_check_return_code  = 200
      health_check_scheme       = "http"
      health_check_start_period = 5
      health_check_timeout      = 5
      health_check_type         = "http"
      instant_deploy            = false
      is_auto_deploy_enabled    = true
      is_force_https_enabled    = true
      is_gzip_enabled           = true
      is_spa                    = false
      is_static                 = false
      is_stripprefix_enabled    = true
      max_restart_count         = 0
      name                      = "app.confitura.pl"
      ports_exposes             = "80"
      project_uuid              = "r4w0wgc04o0swokooc488owg"
      redeploy_on_update        = false
      redirect                  = "both"
      server_uuid               = "yw0ksg404c4kw8cggskw084k"
      static_image              = "nginx:alpine"
      use_build_server          = false
    }

    # Present in Coolify but not managed here (see infra/README.md):
    #   VITE_API_URL
    #   VITE_SELF_URL
    env_vars = {
    }
  }

  backend = {
    app = {
      connect_to_docker_network = false
      docker_image              = "ghcr.io/confitura/confitura-backend"
      docker_images_to_keep     = 2
      domains                   = "https://api.confitura.pl"
      environment_name          = "production"
      health_check_enabled      = false
      health_check_host         = "localhost"
      health_check_interval     = 5
      health_check_method       = "GET"
      health_check_path         = "/api/actuator/health"
      health_check_retries      = 10
      health_check_return_code  = 200
      health_check_scheme       = "http"
      health_check_start_period = 120
      health_check_timeout      = 5
      health_check_type         = "http"
      instant_deploy            = false
      is_auto_deploy_enabled    = true
      is_force_https_enabled    = true
      is_gzip_enabled           = true
      is_spa                    = false
      is_static                 = false
      is_stripprefix_enabled    = true
      max_restart_count         = 0
      name                      = "api.confitura.pl"
      noindex_domains           = ["https://api.confitura.pl"]
      ports_exposes             = "8080"
      project_uuid              = "r4w0wgc04o0swokooc488owg"
      redeploy_on_update        = false
      redirect                  = "both"
      server_uuid               = "yw0ksg404c4kw8cggskw084k"
      static_image              = "nginx:alpine"
      use_build_server          = false
    }

    env_vars = {
      "ALLEGRO_CLIENT_ID"                                     = { value = "63fd714f2f4440e381db69ee7b5b7d8f" }
      "APP_CORS_ORIGINS_0_"                                   = { value = "https://app.confitura.pl" }
      "APP_CORS_ORIGINS_1_"                                   = { value = "https://2025.confitura.pl" }
      "APP_CORS_ORIGINS_2_"                                   = { value = "http://localhost:3000" }
      "APP_CORS_ORIGINS_3_"                                   = { value = "https://2026.confitura.pl" }
      "APP_CORS_ORIGINS_4"                                    = { value = "https://confitura.pl" }
      "CHAT_DATALINKS_BASE_URL"                               = { value = "https://api.prod.datalinks.com/api/v1" }
      "CHAT_DATALINKS_NAMESPACE"                              = { value = "confitura-2026" }
      "CHAT_DATALINKS_USERNAME"                               = { value = "confiturapl" }
      "CHAT_ENABLED"                                          = { value = "false" }
      "CONFERENCE_C4P_END"                                    = { value = "2026-08-06T23:59:59.0+02:00[Europe/Warsaw]" }
      "CONFERENCE_C4P_START"                                  = { value = "2026-02-01T00:00:00.0+02:00[Europe/Warsaw]" }
      "DB_HOST"                                               = { value = "q8w8sgsk8cgk0gcc084csw00" }
      "DB_NAME"                                               = { value = "confitura" }
      "DB_USER"                                               = { value = "confitura" }
      "FACEBOOK_CALLBACK"                                     = { value = "https://api.confitura.pl/api/login/facebook/callback" }
      "FACEBOOK_KEY"                                          = { value = "1767792773823045" }
      "GITHUB_CALLBACK"                                       = { value = "https://api.confitura.pl/api/login/github/callback" }
      "GITHUB_KEY"                                            = { value = "8cca8837df626c04f1da" }
      "GOOGLE_CALLBACK"                                       = { value = "https://api.confitura.pl/api/login/google/callback" }
      "GOOGLE_KEY"                                            = { value = "378614084166-1lvj014jhbqkhnj9hnngftoi1dqdg4cq.apps.googleusercontent.com" }
      "LISTMONK_BASE_URL"                                     = { value = "https://newsletter.confitura.pl/" }
      "LISTMONK_USERNAME"                                     = { value = "jelatyna" }
      "MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT" = { value = "http://alloy:4318/v1/traces" }
      "NIXPACKS_JDK_VERSION"                                  = { value = "21" }
      "RESOURCES_FOLDER"                                      = { value = "/home/jelatyna/2017/files/new-photos" }
      "RESOURCES_RESOURCES_BASE_URL"                          = { value = "https://api.confitura.pl/api" }
      "SPRING_DATASOURCE_URL"                                 = { value = "jdbc:postgresql://pgw008ck8cg0kwgk88sswoo4:5432/confitura" }
      "SPRING_PROFILES_ACTIVE"                                = { value = "prod,default" }
      "SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE"                = { value = "3MB" }
      "TWITTER_CALLBACK"                                      = { value = "https://api.confitura.pl/api/login/twitter/callback" }
      "TWITTER_KEY"                                           = { value = "VvHrNUvTIm1fR7KA6O3edIVKV" }
    }
  }
}
