#!/bin/bash
# Read-only factory dashboard for a herdr pane (see Herdr in SKILL.md). ONCE=1 prints one frame and exits.
# Never take index.lock, so this view can't collide with the sub-agents' git commands.
export GIT_OPTIONAL_LOCKS=0
cd "$(dirname "$0")" && cd "$(git rev-parse --show-toplevel)" || exit 1

pr_filter='def n($s): map(select(. == $s)) | length;
.[] | select(.headRefName | startswith("factory/")) | .headRefOid as $head
| [.statusCheckRollup[] | .state // (if .status == "COMPLETED" then .conclusion else "PENDING" end)
   | if test("SUCCESS|NEUTRAL|SKIPPED") then "passing" elif test("PENDING|EXPECTED") then "pending" else "failing" end] as $ci
| ([.comments[] | select(.author.login == "macroscopeapp") | .body | select(contains("**Verdict:**"))] | last // "") as $verdict
| "#\(.number) \(.title)",
  "    CI \($ci | n("passing")) passing, \($ci | n("failing")) failing, \($ci | n("pending")) pending"
  + " · Macroscope \(if any(.reviews[]; .author.login == "macroscopeapp" and .state == "APPROVED" and .commit.oid == $head) then "approved"
     elif ($verdict | contains("Needs human approval")) then "needs human" else "pending" end)"
  + " · \(.mergeable | ascii_downcase)"'

# gh at most once a minute, cached between redraws; timeout keeps a slow network from stalling the loop; a missing timeout fails fast (no fallback can stop gh, a Go binary that ignores SIGALRM).
prs_at=-60
refresh_prs() {
  [ $((SECONDS - prs_at)) -ge 60 ] || return
  prs_at=$SECONDS
  if out=$(timeout 20 gh pr list --state open \
      --json number,title,headRefName,headRefOid,mergeable,statusCheckRollup,reviews,comments \
      --jq "$pr_filter" 2>/dev/null); then
    prs=${out:-none}
  else
    prs="gh failed or timed out; retrying in a minute"
  fi
  prs_time=$(date +%H:%M:%S)
}

frame() {
  cat backlog/.factory-phase 2>/dev/null || echo "no phase yet"
  echo
  echo "events:"
  tail -n 10 backlog/.factory-events.log 2>/dev/null
  echo
  echo "board:"
  backlog task list -s "In Progress,In Review" --plain 2>/dev/null
  echo "next ready: $(backlog task list -s "To Do" -l ready-for-agent --ready --plain 2>/dev/null | grep -m1 -o 'TASK-.*' || echo none)"
  echo
  echo "open factory PRs (as of $prs_time):"
  echo "$prs"
  echo
  local base
  base=$(git merge-base HEAD origin/master 2>/dev/null)
  echo "branch: $(git branch --show-current)"
  echo "commits:"
  git log --oneline "$base"..HEAD 2>/dev/null | head -10
  echo "counted diff vs base (incl. uncommitted):$(git diff --shortstat "$base" -- . ':(exclude)openapi.json' \
    ':(exclude)admin-app/src/client' ':(exclude)*pnpm-lock.yaml' ':(exclude)*.terraform.lock.hcl' \
    ':(exclude)jelatyna-backend/src/main/resources/db/migration/V001__baseline.sql' 2>/dev/null)"
  echo "uncommitted:"
  git status --short | head -15
  local r=jelatyna-backend/target/surefire-reports
  if [ -d "$r" ]; then
    local n f
    n=$(ls "$r"/*.txt 2>/dev/null | wc -l | tr -d ' ')
    f=$(grep -l -E 'Failures: [1-9]|Errors: [1-9]' "$r"/*.txt 2>/dev/null | wc -l | tr -d ' ')
    echo
    echo "backend tests: $n classes reported, $f failing (as of $(stat -f %Sm -t %H:%M:%S "$r"))"
  fi
}

# Cut to the ~95-column pane so long titles don't wrap; perl counts UTF-8 characters whatever the locale.
cut95() { perl -CS -lpe '$_ = substr $_, 0, 95'; }
if [ -n "$ONCE" ]; then refresh_prs; frame | cut95; exit; fi
while true; do
  refresh_prs
  out=$(frame | cut95)
  clear
  printf '%s\n' "$out"
  sleep 5
done
