#!/usr/bin/env bash
# Adopt the live Confitura applications into OpenTofu state.
#
#   ./infra/scripts/import-production.sh <inventory-dir> [--execute]
#
# Without --execute it prints the import commands and runs nothing. Read them
# before you let it run anything.
#
# Preconditions, all of them:
#   1. infra/production/production.auto.tfvars lists all three applications,
#      filled from the inventory. `tofu import` refuses an address that is not
#      in the configuration.
#   2. A Coolify export/backup of the current configuration has been taken.
#   3. COOLIFY_TOKEN has read:sensitive (or root).
#   4. AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY are set for the state bucket,
#      and infra/backend-proof has already proved the backend works.
#
# Import only. This script never applies. If a later plan wants to destroy or
# replace an application, stop: prevent_destroy in the module will fail the
# plan, and that is the intended outcome, not a problem to work around.
#
# Why the compound form for applications:
#   project_uuid:server_uuid:environment_name:app_uuid
# Coolify's application GET returns none of the first three, so a plain-UUID
# import leaves them null and needs a follow-up apply, which defeats the
# empty-plan criterion. The compound form also validates that the application
# really is on that server via GET /servers/{uuid}/resources and fails if not.
#
# Environment variables import one at a time as
#   application:<app-uuid>:<env-var-uuid>
# so each variable has its own state address and shows up individually in a
# plan. The env var UUID comes from the inventory, not from the name.

set -euo pipefail

INV="${1:?usage: import-production.sh <inventory-dir> [--execute]}"
EXECUTE="${2:-}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../production" && pwd)"

# B2 cannot lock OpenTofu state, so every state operation passes -lock=false.
# Serialisation is the apply workflow's concurrency group, not the backend.
TOFU=(tofu -chdir="$ROOT" import -lock=false -input=false)

run() {
  if [[ "$EXECUTE" == "--execute" ]]; then
    "${TOFU[@]}" "$@"
  else
    printf 'tofu -chdir=%s import -lock=false %q %q\n' "$ROOT" "$1" "$2"
  fi
}

for key in webpage admin_app backend; do
  app_json="${INV}/app-${key}.json"
  envs_json="${INV}/envs-${key}.json"
  [[ -f "$app_json" ]] || { echo "missing ${app_json}; run coolify-inventory.sh first" >&2; exit 1; }

  app_uuid=$(jq -r '.uuid' "$app_json")
  project_uuid=$(jq -r '.project_uuid // empty' "$app_json")
  server_uuid=$(jq -r '.server_uuid // empty' "$app_json")
  env_name=$(jq -r '.environment_name // "production"' "$app_json")

  # Coolify usually omits these two from the application GET. They come from
  # the projects/servers listing instead; fill them in the inventory document
  # and pass them here rather than guessing. A wrong server_uuid is not
  # corrected on refresh and would recreate the app on the wrong server.
  : "${project_uuid:?project_uuid for ${key} not in inventory - resolve it from projects.json}"
  : "${server_uuid:?server_uuid for ${key} not in inventory - resolve it from servers.json}"

  run "module.app[\"${key}\"].coolify_application_docker_image.this" \
      "${project_uuid}:${server_uuid}:${env_name}:${app_uuid}"

  # Coolify has no "this variable is a secret" flag, so the split is ours: a
  # human judgement recorded during the inventory in <inventory-dir>/secrets.txt
  # as one "<app_key>:<VAR_NAME>" per line. Anything not listed is treated as
  # non-secret and its value goes in production.auto.tfvars in clear.
  # Getting this list wrong in the safe direction (listing too much) only costs
  # readability; getting it wrong the other way commits a secret.
  secret_list="${INV}/secrets.txt"
  while IFS=$'\t' read -r name env_uuid; do
    [[ -z "$name" ]] && continue
    if [[ -f "$secret_list" ]] && grep -qxF "${key}:${name}" "$secret_list"; then
      addr_res=secret
    else
      addr_res=plain
    fi
    run "module.app[\"${key}\"].coolify_environment_variable.${addr_res}[\"${name}\"]" \
        "application:${app_uuid}:${env_uuid}"
  done < <(jq -r '.[] | [.key, .uuid] | @tsv' "$envs_json")
done

if [[ "$EXECUTE" != "--execute" ]]; then
  echo
  echo "Dry run. Re-run with --execute to import. Then:"
  echo "  tofu -chdir=${ROOT} plan -lock=false   # must say: No changes."
fi
