#!/bin/bash
# Opens the factory-watch dashboard pane right of the lead's herdr pane, or reuses it (see Herdr in SKILL.md).
# Takes no arguments, so its permission rule can't carry a command into a pane.
[ "$HERDR_ENV" = 1 ] || exit 0
cd "$(dirname "$0")" && cd "$(git rev-parse --show-toplevel)" || exit 1
herdr pane list --workspace "$HERDR_WORKSPACE_ID" \
  | jq -e '.result.panes[] | select(.label == "factory-watch")' >/dev/null && exit 0
id=$(herdr pane split --current --direction right --cwd "$PWD" --no-focus | jq -re .result.pane.pane_id) || exit 1
herdr pane run "$id" .claude/skills/factory/watch.sh
herdr pane rename "$id" factory-watch
