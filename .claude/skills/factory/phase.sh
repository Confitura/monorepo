#!/bin/bash
# Records the factory's phase for the dashboard (see Herdr in SKILL.md). Arguments are data, never run.
#   phase.sh '<phase line>'  overwrite backlog/.factory-phase, append the line to the event log, title the lead's pane
#   phase.sh --clear         clear the lead's pane title (the iteration stopped)
# Writes only the two files outside herdr; the pane title needs HERDR_ENV=1.
cd "$(dirname "$0")" && cd "$(git rev-parse --show-toplevel)" || exit 1
title() { [ "$HERDR_ENV" != 1 ] || herdr pane report-metadata "$HERDR_PANE_ID" --source factory "$@"; }
if [ "$1" = --clear ]; then title --clear-title; exit; fi
[ -n "$1" ] || { echo "usage: phase.sh '<phase line>' | --clear" >&2; exit 2; }
printf '%s · started %s\n' "$1" "$(date +%H:%M)" > backlog/.factory-phase
log=backlog/.factory-events.log
{ tail -n 199 "$log" 2>/dev/null; printf '%s %s\n' "$(date '+%Y-%m-%d %H:%M')" "$1"; } > "$log.tmp" && mv "$log.tmp" "$log"
title --title "factory · $1"
