---
name: factory
description: Local code factory over the private Backlog.md board. `setup` initializes the board; `run [task-id]` implements one ready task on a fresh branch, has it reviewed, and opens a draft PR; `sync` closes out merged PRs. Run only when the user types /factory.
disable-model-invocation: true
argument-hint: "setup | run [task-id] | sync"
---

# /factory

Subcommand: `$0` (default `run`). Arguments: `$ARGUMENTS`.

Pipeline: Backlog.md task (`ready-for-agent`) → branch `factory/<id>-<slug>` from `origin/master` → `factory-implementer` subagent → independent `factory-reviewer` subagent → draft PR → human merges → `/factory sync` marks it Done.

Board: `backlog browser` → http://localhost:6420. Tracker conventions: `docs/agents/issue-tracker.md`.

## Invariants

1. **One task at a time.** Never start a task while another is `In Progress`.
2. Only you (the lead) run `backlog`. Subagents get the task text in their prompt.
3. Never merge, mark a PR ready, push to `master`, or force-push. PRs are always drafts.
4. The repo is **public**: PR titles, bodies, and commits contain only the task title, acceptance criteria, the implementer's SUMMARY, and the checks. Never paste backlog notes, spec text, or anything private.
5. No `Co-Authored-By` trailers.

## setup

1. `backlog --version` — if missing, tell the user to run `brew install backlog-md` and stop.
2. If `backlog/config.yml` exists, say the board is already set up and skip to step 6.
3. `git check-ignore -q backlog/config.yml` must succeed — otherwise stop: `/backlog/` is missing from `.gitignore`.
4. `backlog init "Confitura" --no-git --integration-mode none` (filesystem-only, no auto-commit, no agent-instruction files).
5. In `backlog/config.yml` set:
   ```yaml
   statuses: ["To Do", "In Progress", "In Review", "Done"]
   labels: ["ready-for-agent", "ready-for-human", "needs-triage", "needs-info", "wontfix"]
   ```
6. Verify: `git status --porcelain` shows no new files (no `AGENTS.md`, no `CLAUDE.md` edits) and `backlog task list --plain` runs.
7. Tell the user: run `backlog browser` in its own terminal; create tasks with `/to-spec` + `/to-tickets` or `backlog task create`.

## run

### Preflight — stop and report if any fails

- `git rev-parse --show-toplevel` is this repo and `git status --porcelain` is empty.
- `backlog/config.yml` exists (else: run `/factory setup`).
- `backlog task list -s "In Progress" --plain` is empty. If not, show the task and ask the user whether to reset it to `To Do` (its branch stays) — don't decide alone.
- `gh auth status` succeeds.
- `git fetch origin --prune`.
- Remember the current branch (`git branch --show-current`) to return to at the end.

### 1. Pick the task

- With an id: `backlog task view <id> --plain`; it must be `To Do`, labelled `ready-for-agent`, and appear in the ready list below. Otherwise explain why and stop.
- Without: the first task from `backlog task list -s "To Do" -l ready-for-agent --ready --plain`. Empty → say the frontier is empty and stop.

`--ready` treats a dependency as satisfied only when it is `Done` (merged), so tasks never build on unmerged work.

### 2. Start

1. `backlog task view <id> --plain` → keep the full text (title, description, acceptance criteria).
2. Branch `B = factory/<id lowercase>-<slug>` where slug is the title in `[a-z0-9-]`, ≤40 chars.
3. `git switch -c <B> origin/master`
4. `backlog task edit <id> -s "In Progress" --append-notes "factory: started <YYYY-MM-DD> on <B>"`

### 3. Implement

Spawn **one** `factory-implementer` subagent (foreground) with this prompt:

```
TASK: <id> — <title>
BRANCH: <B> (already checked out)
<full task text: description + acceptance criteria>
```

### 4. Review

- `STATUS: BLOCKED` → go to **Hand to human**.
- `STATUS: DONE` → confirm `git log --oneline origin/master..HEAD` is non-empty and `git status --porcelain` is clean, then spawn a `factory-reviewer` subagent with: the task text, `BRANCH: <B>`, `BASE: origin/master`, and the implementer's report.
- `VERDICT: FAIL` (first time) → resume the same implementer with `SendMessage` (agent id from its result) carrying the REQUIRED FIXES; if resuming isn't possible, spawn a fresh `factory-implementer` with the task text, the findings, and "remediation mode: the branch already has your earlier commits". Then run a **new** reviewer.
- Second FAIL → **Hand to human**.

### 5. Publish (on PASS)

1. `git push -u origin <B>`
2. `gh pr create --draft --base master --head <B> --title "<task title>" --body-file <tmp file>` with:
   ```
   ## Summary
   <implementer SUMMARY>

   ## Acceptance criteria
   - [x] <each criterion>

   ## Verification
   - <each CHECKS line>

   Draft opened by the local code factory after an automated review PASS. Needs human review and merge.
   ```
3. `backlog task edit <id> -s "In Review" --ref <PR url> --append-notes "factory: draft PR <url>, reviewer PASS"` plus `--check-ac <n>` for each criterion the reviewer marked MET.

### Hand to human

Don't push. `backlog task edit <id> -s "To Do" --remove-label ready-for-agent --add-label ready-for-human --append-notes "<blocker or reviewer findings>; local branch <B>"`.

### 6. Finish

`git switch <remembered branch>`. Report: task, branch, verdict, PR url (or what the human must decide), and the next ready task from the frontier.

## sync

1. `backlog task list -s "In Review" --plain`. For each task, read its PR url from `References:` in `backlog task view <id> --plain`, then `gh pr view <url> --json state,headRefName,isDraft`.
   - `MERGED` → `backlog task edit <id> -s "Done" --append-notes "factory: merged <YYYY-MM-DD>"`; delete the local branch with `git branch -D <headRefName>` if it exists and isn't checked out (squash merges aren't ancestors, so `-d` fails).
   - `CLOSED` → `backlog task edit <id> -s "To Do" --remove-label ready-for-agent --add-label ready-for-human --append-notes "factory: PR closed unmerged"`.
   - `OPEN` → report draft state and a one-line `gh pr checks <url>` summary.
2. `git fetch origin --prune`.
3. Print a table of what changed and the new frontier (`backlog task list -s "To Do" -l ready-for-agent --ready --plain`).
