---
name: factory
description: Local code factory over the private Backlog.md board. `setup` initializes the board; `run [task-id]` implements one ready task on a fresh branch, has it reviewed, and on a PASS opens a PR ready for review; `fix [pr]` resolves an open factory PR's merge conflict or failing checks with a reviewed commit on its branch; `sync` closes out merged PRs; `auto` runs one unattended iteration. Run only when  the user types /factory, or from a /loop the user started with /factory auto.
argument-hint: "setup | run [task-id] | fix [pr] | sync | auto"
---

# /factory

Subcommand: `$0` (default `run`). Arguments: `$ARGUMENTS`.

Pipeline: Backlog.md task (`ready-for-agent`) → branch `factory/<id>-<slug>` from `origin/master` → `factory-implementer` subagent → independent `factory-reviewer` subagent → PR ready for review → human reviews and merges → `/factory sync` marks it Done.

Board: `backlog browser` → http://localhost:6420. Tracker conventions: `docs/agents/issue-tracker.md`.

## Invariants

1. **One task at a time.** Never start a task while another is `In Progress`.
2. Only you (the lead) run `backlog`. Subagents get the task text in their prompt.
3. Never merge a PR, push to `master`, or force-push. Open a PR, or push to an open PR's branch, only after a reviewer PASS (a merge of `master` that needed no conflict resolution excepted), and open it ready for review, never as a draft. Never mark an existing PR ready or push to a draft: a draft means a human put it there. Bring `master` into a PR branch with a merge, never a rebase: a rebase needs a force push. A human still reviews and merges every PR.
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

**Size budget.** A PR stays around **300 changed lines** (added plus deleted), hand-written only. Generated or vendored files don't count: `openapi.json`, `admin-app/src/client/**`, lockfiles (`pnpm-lock.yaml`, `.terraform.lock.hcl`), and verbatim schema dumps such as the Flyway baseline (`V001__baseline.sql`). The measure, run from the branch against `origin/master`:

```
git diff --shortstat origin/master...HEAD -- . ':(exclude)openapi.json' ':(exclude)admin-app/src/client' ':(exclude)*pnpm-lock.yaml' ':(exclude)*.terraform.lock.hcl' ':(exclude)jelatyna-backend/src/main/resources/db/migration/V001__baseline.sql'
```

If the task clearly won't fit in about 300 counted lines, split it **before branching**; the maintainer prefers several small PRs over one big one. Only split where each slice is a complete, green, reviewable change that leaves `master` working and wired (a migration and the entity change that uses it stay together, and may exceed the budget if the PR says why):

1. Cut sequential slices. The original task becomes the first slice, retitled to what it now covers (its PR takes the task title): `backlog task edit <id> --title "<slice 1 title>" --description "<slice 1>" --acceptance-criteria "<criterion>" …` (repeat `--acceptance-criteria` per criterion). It **replaces the whole acceptance-criteria list**; it doesn't append (`--ac` adds one, `--remove-ac <index>` removes one).
2. Create each later slice with `backlog task create "<title>" -d "<description>" --ac "<criterion>" … -l ready-for-agent --dep <previous slice id>`, so each depends on the one before and the `--ready` frontier keeps them in order. Carry over a `model:` / `effort:` label only if the human set it on the original; leave the rest unlabelled so each slice is tiered on its own when it runs.
3. Tasks that depended on the original would otherwise become ready as soon as slice 1 merges. Find them (`backlog task list --plain`, then `backlog task view <id> --plain` for each) and add the last slice to their dependencies: `backlog task edit <dependent> --dep <its existing dependencies>,<last slice id>` (`--dep` replaces the list, so repeat the existing ones).
4. Record it: `backlog task edit <id> --append-notes "factory: split into <id>, <new ids> — <one-line reason>"`.
5. Run only the first slice (the original task) through the rest of `run`. In `auto` mode, also notify `split:<id>`.

When in doubt, don't split: the implementer stops at a green checkpoint if the diff runs over (see step 5).

The implementer runs on the model and effort in two task labels: `model:<haiku|sonnet|opus|fable>` and `effort:<low|medium|high|xhigh|max>`. Labels the human already set win; fill any missing one from the tier you pick:

| Tier | Model / effort | When the task… |
|---|---|---|
| XS | `haiku` / `low` | is a typo or one-line config change |
| S | `sonnet` / `medium` | changes docs or is mechanical |
| M | `sonnet` / `high` | touches one module and needs tests |
| L | `opus` / `xhigh` | touches 2+ modules, changes the API (`openapi.json` + client), or touches security/auth, schema, or data |

Judge from the description and acceptance criteria (of the first slice, after any split); when torn between two tiers, pick the higher.

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
- `REMAINING WORK` in the report that isn't `none` means the implementer stopped at a green checkpoint to stay near the size budget. The reviewer marks the criteria listed there `DEFERRED`, which is not a FAIL. Publish what's done as usual, then in **Publish** create the follow-up task for the remainder.
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
   - [x] <each criterion the reviewer marked MET>
   - [ ] <each criterion marked DEFERRED> — deferred to a follow-up task

   ## Verification
   - <each CHECKS line>
   - Size: <N> counted changed lines (<shortstat output>, generated files excluded)<; over budget because <the implementer's stated reason>, if over 300>
   - Automated review: PASS on first round | PASS after one fix round (<what the first review caught and how it was fixed>)
   - Implementer: <m> / <e> (tier <X>) · Reviewer: opus / xhigh

   Opened by the local code factory after an automated review PASS. Needs human review and merge.
   ```
   If the WHY doesn't answer an obvious "why this way?" about the diff, ask the implementer to complete it before publishing — never fill it in from backlog notes.
3. `backlog task edit <id> -s "In Review" --ref <PR url> --append-notes "factory: PR <url> (ready for review), reviewer PASS"` plus `--check-ac <n>` for each criterion the reviewer marked MET (not the DEFERRED ones).
4. If the report's `REMAINING WORK` isn't `none`, create the follow-up: `backlog task create "<title>" -d "<what's left>" --ac "<criterion>" … -l ready-for-agent --dep <id>` with the deferred criteria as its `--ac`s. Carry over a `model:` / `effort:` label only if the human set it (the ones the factory added are recorded in a `factory: tier` note; leave those off so the follow-up is tiered on its own). Append `factory: remaining work split off as <new id>` to the task's notes. It depends on this task, so it stays off the frontier until the PR merges. As in a split, add the new task to the dependencies of any task that depended on this one (`--dep` replaces the list, so repeat the existing ones).

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

## fix

Repairs one open factory PR on its own branch: merges `master` in and resolves the conflicts, or makes its failing checks (CI jobs, CodeQL alerts) pass, so the maintainer reviews PRs that pass and merge cleanly. `auto` runs it; `/factory fix [pr]` runs it by hand. The task stays `In Review` throughout.

### Preflight

As in `run`: a clean tree, no task `In Progress`, `gh auth status`, `git fetch origin --prune`, and remember the current branch.

### 1. Pick the PR

Candidates are the PRs of `In Review` tasks (`References:` in `backlog task view <id> --plain`) that are open and not drafts (a draft means a human turned it back), that either conflict with `master` (`gh pr view <url> --json mergeable` says `CONFLICTING`) or have all their checks finished with at least one failure, and that have attempts left. A PR with both is repaired for the conflict first; its checks run again after the push.

Attempts: at most one per head commit and two per PR, recorded in `backlog/.factory-auto.json` as `fix:<pr>:<head sha>`. A PR whose current head already has a key, or that has two `fix:` keys, has none left. It isn't a candidate: report it, and in `auto` mode notify `pr-conflict:<pr>:<head sha>` or `pr-checks-failed:<pr>:<head sha>`.

With a PR number it must be a candidate; without, take the first. Record its `fix:` key before going on.

### 2. Collect the failures

A conflict needs nothing collected; step 3 starts the merge. For failing checks, subagents can't run `gh`, so gather the evidence yourself:
- `gh pr checks <url>` lists the failing checks and their links.
- A GitHub Actions job (link `…/actions/runs/<run id>/job/<job id>`): the last ~150 lines of `gh run view <run id> --job <job id> --log-failed`.
- CodeQL: `gh api "repos/{owner}/{repo}/code-scanning/alerts?pr=<n>&state=open" --jq '.[] | {rule: .rule.id, message: .most_recent_instance.message.text, path: .most_recent_instance.location.path, line: .most_recent_instance.location.start_line}'`.
- Any other check: its summary. If nothing actionable comes out of any check, go to **Hand back**.

### 3. Fix and review

1. `git switch --no-track -C <headRefName> origin/<headRefName>` (`-C` resets a stale local copy to what the PR has). Note `git rev-parse --short HEAD` as the PR's previous head.
2. For a conflict, `git merge --no-edit origin/master`. If it completes on its own, there's nothing hand-written to review: push it (step 4) and let CI check it. A check that then fails comes back through `fix`. If it stops on conflicts, leave the merge in progress for the implementer.
3. Spawn **one** `factory-implementer` with the task's `model:` / `effort:` labels and this prompt:
   ```
   TASK: <id> — <title>
   BRANCH: <headRefName> (already checked out; it is PR <url>)
   <full task text: description + acceptance criteria>
   FAILING CHECKS:
   <per check: its name, then the log tail or the alert>
   ```
   For a conflict, replace the `FAILING CHECKS` block with `MERGE CONFLICT: a merge of origin/master is in progress; conflicting files: <git diff --name-only --diff-filter=U>`.
4. `STATUS: BLOCKED` → **Hand back** (if a merge is still in progress, `git merge --abort` first). `STATUS: DONE` → confirm `git log --oneline origin/<headRefName>..HEAD` is non-empty and `git status --porcelain` is clean, then spawn a `factory-reviewer` with the task text, `BRANCH: <headRefName>`, `BASE: origin/master`, `PREVIOUS HEAD: <sha>`, the FAILING CHECKS or MERGE CONFLICT block, and the implementer's report.
5. A first `VERDICT: FAIL` gets one fix round, as in `run` step 5. A second FAIL → **Hand back**.

### 4. Push (on PASS)

1. `git push origin <headRefName>`. It's a plain push: if it's rejected because the branch moved, go to **Hand back**. Never force.
2. Add one line at the end of the PR's `## Verification` list: read the body (`gh pr view <url> --json body`), add the line, then `gh pr edit <url> --body-file <tmp file>`. The line: `- Check fix (<short sha>): <the implementer's TLDR>`, or for a conflict `- Merged master (<short sha>): <the implementer's TLDR, or "no conflicts">`. Like the rest of the PR, it's public.
3. `backlog task edit <id> --append-notes "factory: fixed <the conflict | failing checks> on <PR url> in <short sha>, reviewer PASS"`.

### Hand back

Don't push. `backlog task edit <id> --append-notes "factory: couldn't fix <the conflict | failing checks> on <PR url> — <one line>"`. The task stays `In Review` for the human. In `auto` mode, notify `pr-conflict:<pr>:<head sha>` or `pr-checks-failed:<pr>:<head sha>` with that line.

### Finish

`git switch <remembered branch>`. Report: the PR, the conflict or its failing checks, the verdict, and the pushed commit or why it was handed back.

## auto

One unattended iteration, meant for `/loop /factory auto` in a dedicated clone of the repo (so it never switches branches under someone's IDE). It does `sync`, then at most one `fix` or one `run`, and tells the human only when they're needed.

**Notifications.** Send them with the `PushNotification` tool, one short line each (what happened, the id or PR, what to do). Send each event once: keep the keys already sent in `backlog/.factory-auto.json` (gitignored with the board) and skip a key that's there (the same file holds `fix`'s attempt keys). Keys: `pr-opened:<pr>`, `pr-checks-failed:<pr>:<head sha>`, `pr-conflict:<pr>:<head sha>`, `handed:<task>`, `split:<task>`, `human-ready:<task>`, `stuck:<task>`, `idle:<sorted open PR numbers>`, `clone-dirty`.

1. **Clone check.** The tree must be clean and on `master`. If not, notify `clone-dirty` and stop. Then `git fetch origin --prune` and fast-forward `master` (`git merge --ff-only origin/master`); if that fails, notify `clone-dirty` and stop.
2. **`sync`** as above.
3. **Interrupted work.** If a task is `In Progress`, an earlier iteration didn't finish. Notify `stuck:<task>` and stop; never reset it alone.
4. **Conflicts and failing checks.** If `fix` has a candidate PR (its step 1, which also notifies the PRs out of attempts), run `fix` on it exactly as in `fix` (the remembered branch is `master`), then stop: that fix is this iteration's one piece of work.
5. **Human tasks.** For each `To Do` task labelled `ready-for-human` whose dependencies are all `Done`, notify `human-ready:<task>`.
6. **Pick.** Take the first task from the ready frontier. If there is none, notify `idle:<open PRs>` ("nothing ready; waiting on your merges of #…") and stop.
7. **`run`** that task exactly as in `run` (preflight through Finish; the remembered branch is `master`). After Publish, notify `pr-opened:<pr>`. After Hand to human, notify `handed:<task>` with the blocker in one line. If the size check split the task, notify `split:<task>` with the new task ids in one line.
8. Stop after that one task or fix. In `/loop` dynamic mode, schedule the next iteration soon (about a minute) if the frontier still has a ready task or `fix` still has a candidate, otherwise 20–30 minutes.

All invariants apply unchanged: one task at a time, never merge a PR, never push to `master`, and open PRs or push to their branches only after a PASS.
