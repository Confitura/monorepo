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
#   5. Coolify reports build_pack "dockerimage" for all three applications. This
#      one is checked below and the script refuses to import without it, because
#      the resource type cannot be changed later without a destroy and recreate.
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

# Pre-flight: validate all three applications before importing any of them.
#
# This runs as its own pass on purpose. A check inside the import loop would
# refuse the third application only after the first two were already in state,
# leaving a half-adopted environment to unpick by hand. Everything that can be
# known from the inventory alone is therefore settled here, while the cost of
# stopping is still zero.
for key in webpage admin_app backend; do
  app_json="${INV}/app-${key}.json"
  envs_json="${INV}/envs-${key}.json"
  for f in "$app_json" "$envs_json"; do
    [[ -f "$f" ]] || { echo "missing ${f}; run coolify-inventory.sh first" >&2; exit 1; }
  done

  # Coolify usually omits project_uuid and server_uuid from the application GET.
  # They come from the projects/servers listing instead; fill them in the
  # inventory rather than guessing. A wrong server_uuid is not corrected on
  # refresh and would recreate the app on the wrong server.
  project_uuid=$(jq -r '.project_uuid // empty' "$app_json")
  server_uuid=$(jq -r '.server_uuid // empty' "$app_json")
  : "${project_uuid:?project_uuid for ${key} not in inventory - resolve it from projects.json}"
  : "${server_uuid:?server_uuid for ${key} not in inventory - resolve it from servers.json}"
  app_uuid=$(jq -r '.uuid // empty' "$app_json")
  : "${app_uuid:?uuid for ${key} not in inventory - re-run coolify-inventory.sh}"

  # The resource type is the one irreversible choice on this task: changing an
  # application's type later destroys and recreates it. modules/coolify-app uses
  # coolify_application_docker_image, which is only the right type if Coolify
  # reports build_pack "dockerimage". That is still unconfirmed against the live
  # API - the module's type is an expectation, not a verified fact - so assert it
  # here, where it would otherwise be acted on, instead of trusting the module.
  build_pack=$(jq -r '.build_pack // empty' "$app_json")
  if [[ "$build_pack" != "dockerimage" ]]; then
    cat >&2 <<EOF
refusing to import: Coolify reports build_pack "${build_pack:-<absent>}" for ${key}, expected "dockerimage".

modules/coolify-app declares coolify_application_docker_image. If the live build
pack is anything else, that resource type is wrong for this application, and
importing into it risks a destroy-and-recreate of a running conference service.

Nothing has been imported - this check runs before the first import. Take the raw
inventory for ${key} to Reviewer and settle the resource type before importing.
Do not relax this check to let the import through.
EOF
    exit 1
  fi
done

for key in webpage admin_app backend; do
  app_json="${INV}/app-${key}.json"
  envs_json="${INV}/envs-${key}.json"

  app_uuid=$(jq -r '.uuid' "$app_json")
  project_uuid=$(jq -r '.project_uuid' "$app_json")
  server_uuid=$(jq -r '.server_uuid' "$app_json")
  env_name=$(jq -r '.environment_name // "production"' "$app_json")

  run "module.app[\"${key}\"].coolify_application_docker_image.this" \
      "${project_uuid}:${server_uuid}:${env_name}:${app_uuid}"

  # Coolify has no "this variable is a secret" flag, so the split is ours: a
  # human judgement recorded during the inventory in <inventory-dir>/secrets.txt
  # as one "<app_key>:<VAR_NAME>" per line. Anything not listed is treated as
  # non-secret and its value goes in production.auto.tfvars in clear.
  # Getting this list wrong in the safe direction (listing too much) only costs
  # readability; getting it wrong the other way commits a secret.
  #
  # Not imported at all:
  #   - preview copies (is_preview). Coolify keeps one per variable under the
  #     same name, so importing them would put two variables on one address.
  #   - names that are not shell identifiers (dots, brackets). The provider
  #     rejects them, so they cannot have a configuration address.
  #   - names listed in <inventory-dir>/unmanaged.txt, same format as
  #     secrets.txt, for variables left out of production.auto.tfvars on purpose.
  secret_list="${INV}/secrets.txt"
  unmanaged_list="${INV}/unmanaged.txt"
  while IFS=$'\t' read -r name env_uuid; do
    [[ -z "$name" ]] && continue
    if [[ -f "$unmanaged_list" ]] && grep -qxF "${key}:${name}" "$unmanaged_list"; then
      continue
    fi
    if [[ -f "$secret_list" ]] && grep -qxF "${key}:${name}" "$secret_list"; then
      addr_res=secret
    else
      addr_res=plain
    fi
    run "module.app[\"${key}\"].coolify_environment_variable.${addr_res}[\"${name}\"]" \
        "application:${app_uuid}:${env_uuid}"
  done < <(jq -r '.[]
    | select((.is_preview | not) and (.key | test("^[A-Za-z_][A-Za-z0-9_]*$")))
    | [.key, .uuid] | @tsv' "$envs_json")
done

if [[ "$EXECUTE" != "--execute" ]]; then
  echo
  echo "Dry run. Re-run with --execute to import. Then:"
  echo "  tofu -chdir=${ROOT} plan -lock=false   # must say: No changes."
fi
