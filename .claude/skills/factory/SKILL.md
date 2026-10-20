---
name: factory
description: Local code factory over the private Backlog.md board. `setup` initializes the board; `run [task-id]` implements one ready task on a fresh branch, has it reviewed, and on a PASS opens a PR ready for review; `sync` closes out merged PRs. Run only when the user types /factory.
disable-model-invocation: true
argument-hint: "setup | run [task-id] | sync | auto"
---

# /factory

Subcommand: `$0` (default `run`). Arguments: `$ARGUMENTS`.

Pipeline: Backlog.md task (`ready-for-agent`) → branch `factory/<id>-<slug>` from `origin/master` → `factory-implementer` subagent → independent `factory-reviewer` subagent → PR ready for review → human reviews and merges → `/factory sync` marks it Done.

Board: `backlog browser` → http://localhost:6420. Tracker conventions: `docs/agents/issue-tracker.md`.

## Invariants

1. **One task at a time.** Never start a task while another is `In Progress`.
2. Only you (the lead) run `backlog`. Subagents get the task text in their prompt.
3. Never merge, push to `master`, or force-push. Open a PR only after a reviewer PASS, and open it ready for review, never as a draft. Never mark an existing PR ready: a draft means a human put it there. A human still reviews and merges every PR.
4. The repo is **public**: PR titles, bodies, and commits contain only the task title, acceptance criteria, the implementer's TLDR and WHY, the review outcome, and the checks. Never paste backlog notes, spec text, or anything private.
5. Every PR follows `## Pull requests` in `CLAUDE.md`: TL;DR first, then `## Why`, so the PR still explains itself after the task is archived.
6. No `Co-Authored-By` trailers.

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

### 2. Assign model and effort

`backlog task view <id> --plain` → keep the full text (title, description, acceptance criteria, labels).

The implementer runs on the model and effort in two task labels: `model:<haiku|sonnet|opus|fable>` and `effort:<low|medium|high|xhigh|max>`. Labels the human already set win; fill any missing one from the tier you pick:

| Tier | Model / effort | When the task… |
|---|---|---|
| XS | `haiku` / `low` | is a typo or one-line config change |
| S | `sonnet` / `medium` | changes docs or is mechanical |
| M | `sonnet` / `high` | touches one module and needs tests |
| L | `opus` / `xhigh` | touches 2+ modules, changes the API (`openapi.json` + client), or touches security/auth, schema, or data |

Judge from the description and acceptance criteria; when torn between two tiers, pick the higher.

1. A `model:` or `effort:` label with any other value → stop (before branching) and tell the user which label is invalid.
2. Add only the missing labels and record why: `backlog task edit <id> --add-label model:<m> --add-label effort:<e> --append-notes "factory: tier <X> (<m> / <e>) — <one-line reason>"`. If the human set both labels, the tier is `override`; just append the note.

The reviewer ignores these labels — it is pinned to `opus` / `xhigh` in its own definition. Never pass `model` or `effort` when spawning it.

### 3. Start

1. Branch `B = factory/<id lowercase>-<slug>` where slug is the title in `[a-z0-9-]`, ≤40 chars. If `B` already exists (`git rev-parse --verify --quiet <B>` or `origin/<B>` succeeds — an earlier attempt), append `-2`, `-3`, … until it's free. A retry always starts fresh from `origin/master`; the earlier branch stays for reference and `sync` deletes it once the task is merged.
2. `git switch --no-track -c <B> origin/master` (`--no-track` so the branch never tracks `master`)
3. `backlog task edit <id> -s "In Progress" --append-notes "factory: started <YYYY-MM-DD> on <B>"`

### 4. Implement

Spawn **one** `factory-implementer` subagent (foreground) with `model: <m>` and `effort: <e>` from step 2, and this prompt:

```
TASK: <id> — <title>
BRANCH: <B> (already checked out)
<full task text: description + acceptance criteria>
```

### 5. Review

- `STATUS: BLOCKED` → go to **Hand to human**.
- `STATUS: DONE` → confirm `git log --oneline origin/master..HEAD` is non-empty and `git status --porcelain` is clean, then spawn a `factory-reviewer` subagent with: the task text, `BRANCH: <B>`, `BASE: origin/master`, and the implementer's report.
- `VERDICT: FAIL` (first time) → resume the same implementer with `SendMessage` (agent id from its result) carrying the REQUIRED FIXES — it keeps its model and effort. If resuming isn't possible, spawn a fresh `factory-implementer` with the same `model` / `effort`, the task text, the findings, and "remediation mode: the branch already has your earlier commits". Then run a **new** reviewer.
- Second FAIL → **Hand to human**.

### 6. Publish (on PASS)

1. `git push -u origin <B>`
2. `gh pr create --base master --head <B> --title "<task title>" --body-file <tmp file>` with:
   ```
   <implementer TLDR>

   ## Why
   <implementer WHY — Problem / Approach / Alternatives considered / Trade-offs / follow-ups>

   ## Acceptance criteria
   - [x] <each criterion>

   ## Verification
   - <each CHECKS line>
   - Automated review: PASS on first round | PASS after one fix round (<what the first review caught and how it was fixed>)
   - Implementer: <m> / <e> (tier <X>) · Reviewer: opus / xhigh

   Opened by the local code factory after an automated review PASS. Needs human review and merge.
   ```
   If the WHY doesn't answer an obvious "why this way?" about the diff, ask the implementer to complete it before publishing — never fill it in from backlog notes.
3. `backlog task edit <id> -s "In Review" --ref <PR url> --append-notes "factory: PR <url> (ready for review), reviewer PASS"` plus `--check-ac <n>` for each criterion the reviewer marked MET.

### Hand to human

Don't push. `backlog task edit <id> -s "To Do" --remove-label ready-for-agent --add-label ready-for-human --append-notes "<blocker or reviewer findings>; local branch <B>"`.

### 7. Finish

`git switch <remembered branch>`. Report: task, tier with model / effort, branch, verdict, PR url (or what the human must decide), and the next ready task from the frontier.

## sync

1. `backlog task list -s "In Review" --plain`. For each task, read its PR url from `References:` in `backlog task view <id> --plain`, then `gh pr view <url> --json state,headRefName,isDraft`.
   - `MERGED` → `backlog task edit <id> -s "Done" --append-notes "factory: merged <YYYY-MM-DD>"`; delete the task's local branches — `<headRefName>` plus any earlier attempts from `git branch --list 'factory/<id lowercase>-*'` — with `git branch -D`, skipping one that is checked out (squash merges aren't ancestors, so `-d` fails).
   - `CLOSED` → `backlog task edit <id> -s "To Do" --remove-label ready-for-agent --add-label ready-for-human --append-notes "factory: PR closed unmerged"`.
   - `OPEN` → report whether it's ready or a draft (a draft means a human turned it back) and a one-line `gh pr checks <url>` summary.
2. `git fetch origin --prune`.
3. Print a table of what changed and the new frontier (`backlog task list -s "To Do" -l ready-for-agent --ready --plain`).

## auto

One unattended iteration, meant for `/loop /factory auto` in a dedicated clone of the repo (so it never switches branches under someone's IDE). It does `sync`, then at most one `run`, and tells the human only when they're needed.

**Notifications.** Send them with the `PushNotification` tool, one short line each (what happened, the id or PR, what to do). Send each event once: keep the keys already sent in `backlog/.factory-auto.json` (gitignored with the board) and skip a key that's there. Keys: `pr-opened:<pr>`, `pr-checks-failed:<pr>:<head sha>`, `pr-conflict:<pr>:<head sha>`, `handed:<task>`, `human-ready:<task>`, `stuck:<task>`, `idle:<sorted open PR numbers>`, `clone-dirty`.

1. **Clone check.** The tree must be clean and on `master`. If not, notify `clone-dirty` and stop. Then `git fetch origin --prune` and fast-forward `master` (`git merge --ff-only origin/master`); if that fails, notify `clone-dirty` and stop.
2. **`sync`** as above.
3. **Open PRs.** For each task still `In Review`: if `gh pr checks <url>` shows a failure, notify `pr-checks-failed`; if `gh pr view <url> --json mergeable` says `CONFLICTING`, notify `pr-conflict`. Don't fix either yourself.
4. **Interrupted work.** If a task is `In Progress`, an earlier iteration didn't finish. Notify `stuck:<task>` and stop; never reset it alone.
5. **Human tasks.** For each `To Do` task labelled `ready-for-human` whose dependencies are all `Done`, notify `human-ready:<task>`.
6. **Pick.** Take the first task from the ready frontier. If there is none, notify `idle:<open PRs>` ("nothing ready; waiting on your merges of #…") and stop.
7. **`run`** that task exactly as in `run` (preflight through Finish; the remembered branch is `master`). After Publish, notify `pr-opened:<pr>`. After Hand to human, notify `handed:<task>` with the blocker in one line.
8. Stop after that one task. In `/loop` dynamic mode, schedule the next iteration soon (about a minute) if the frontier still has a ready task, otherwise 20–30 minutes.

All invariants apply unchanged: one task at a time, never merge, never push to `master`, PRs ready for review only after a PASS.
