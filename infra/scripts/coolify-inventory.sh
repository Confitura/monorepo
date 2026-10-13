#!/usr/bin/env bash
# Read-only inventory of the Confitura Coolify configuration.
#
# Writes one JSON file per application plus the project and server lists to an
# output directory, and prints a short summary. It performs GETs only; there is
# no write, no deploy and no restart in this script.
#
#   COOLIFY_TOKEN=... ./infra/scripts/coolify-inventory.sh out/
#
# The token needs read:sensitive (or root). With a default-permission token
# Coolify returns empty strings for sensitive fields, the inventory silently
# records blanks, and `tofu plan` then never converges. The script checks for
# this and warns.
#
# Route names were confirmed against https://admin.confitura.pl on 2026-10-05:
# every path below answers 401 unauthenticated, while a made-up path answers
# 404, so these routes exist on this instance.
#
# Output is configuration, and configuration contains secrets. Write it outside
# the repository and delete it when the inventory document is written. Never
# commit it, never paste a value into an issue.

set -euo pipefail

ENDPOINT="${COOLIFY_ENDPOINT:-https://admin.confitura.pl}"
OUT="${1:?usage: coolify-inventory.sh <output-dir>}"

if [[ -z "${COOLIFY_TOKEN:-}" ]]; then
  echo "COOLIFY_TOKEN is not set" >&2
  exit 1
fi

# The three in-scope applications. UUIDs are the deploy?uuid= targets in
# .github/workflows/deploy-images.yml. Nothing else is in scope: no databases,
# no other project, no server-level settings.
declare -A APPS=(
  [webpage]=jjbrvlzsq0ck1oqx46b0i0v6
  [admin_app]=a4z9gvkuf4vk784dxd39nj2f
  [backend]=wqwwmtfjn4xuhsgj4uzwfd4x
)

mkdir -p "$OUT"
chmod 700 "$OUT"

api() {
  local path="$1" out="$2"
  local code
  code=$(curl -sS -o "$out" -w '%{http_code}' \
    -H "Authorization: Bearer ${COOLIFY_TOKEN}" \
    -H 'Accept: application/json' \
    "${ENDPOINT}/api/v1/${path}")
  if [[ "$code" != "200" ]]; then
    echo "GET /api/v1/${path} -> HTTP ${code}" >&2
    [[ "$code" == "403" ]] && echo "  403 means the token lacks an ability. Report which path failed." >&2
    return 1
  fi
}

echo "== instance =="
api "version" "$OUT/version.json" && echo "Coolify $(tr -d '\"' < "$OUT/version.json")"

echo "== projects and servers (for project_uuid / server_uuid) =="
api "projects" "$OUT/projects.json"
api "servers" "$OUT/servers.json"

for key in "${!APPS[@]}"; do
  uuid="${APPS[$key]}"
  echo "== ${key} (${uuid}) =="
  api "applications/${uuid}" "$OUT/app-${key}.json"
  api "applications/${uuid}/envs" "$OUT/envs-${key}.json"

  # build_pack decides the resource type, and the resource type is the one
  # irreversible choice here: changing an application's type later destroys and
  # recreates it. Expect "dockerimage" for all three.
  echo -n "  build_pack: "
  jq -r '.build_pack // "MISSING"' "$OUT/app-${key}.json"
  echo -n "  docker_image / tag: "
  jq -r '"\(.docker_registry_image_name // "null") : \(.docker_registry_image_tag // "null")"' "$OUT/app-${key}.json"
  echo -n "  env var count: "
  jq -r 'length' "$OUT/envs-${key}.json"

  # If values come back empty while Coolify shows them in the UI, the token is
  # missing read:sensitive and the empty plan is unreachable.
  blanks=$(jq -r '[.[] | select((.value // "") == "")] | length' "$OUT/envs-${key}.json")
  if [[ "$blanks" != "0" ]]; then
    echo "  WARNING: ${blanks} variable(s) came back with an empty value."
    echo "           Check them in the Coolify UI. If the UI shows a value, the"
    echo "           token lacks read:sensitive - stop and get a better token."
  fi
done

echo
echo "Written to ${OUT}. This directory holds secret values: keep it out of the repo and delete it when done."
