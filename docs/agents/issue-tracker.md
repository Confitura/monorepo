# Issue tracker: Backlog.md (local, private)

Specs and tickets for this repo live on a local [Backlog.md](https://github.com/MrLesk/Backlog.md) board in `backlog/`. The directory is **gitignored** (the repo is public) and initialized filesystem-only (`--no-git`, no auto-commit). Use the `backlog` CLI from the repo root; don't hand-edit task files. Run `/factory setup` once to create the board, and `backlog browser` to view it at http://localhost:6420.

Statuses: `To Do` → `In Progress` → `In Review` (draft PR open) → `Done` (merged). Triage roles are labels — see `triage-labels.md`.

Optional labels `model:<haiku|sonnet|opus|fable>` and `effort:<low|medium|high|xhigh|max>` pick the model and effort `/factory run` uses for a task's implementer. Leave them off and the factory assigns a tier and adds them itself; set one or both to override. The reviewer is always `opus` / `xhigh`.

## Conventions

- **Create a ticket**: `backlog task create "<title>" -d "<body>" --ac "<criterion>" --ac "<criterion>" -l ready-for-agent --dep task-3 --doc "<spec doc path>" --plain`. One `--ac` per acceptance criterion (not in the body). Put real newlines in `-d`; wrap text containing backticks in single quotes.
- **Read a ticket**: `backlog task view <id> --plain`
- **List tickets**: `backlog task list --plain` with `-s "<status>"`, `-l <label>`, `--ready` (unblocked: every dependency `Done`), or `backlog search "<query>" --plain`.
- **Comment**: `backlog task edit <id> --append-notes "<text>"`
- **Apply / remove labels**: `backlog task edit <id> --add-label <label>` / `--remove-label <label>`
- **Close**: `backlog task edit <id> -s "Done"`. Won't fix: `--add-label wontfix`, then `backlog task archive <id>`.

## When a skill says "publish to the issue tracker"

- **Spec / PRD** → `backlog doc create "<title>" -t specification` prints the path (`backlog/docs/doc-<n> - <Title>.md`); write the spec body into that file below its frontmatter. Docs carry no labels, so the `ready-for-agent` label goes on the tickets.
- **Tickets** → one `backlog task create` per ticket, blockers first so `--dep` can name real ids. Link the spec with `--doc`. Label `ready-for-agent` so `/factory run` picks them up.

## When a skill says "fetch the relevant ticket"

`backlog task view <id> --plain` (or `backlog doc view doc-<n> --plain` for a spec).

## Pull requests as a triage surface

**PRs as a request surface: no.**

## Wayfinding operations

Used by `/wayfinder`.

- **Map**: a task labelled `wayfinder:map` holding the Notes / Decisions-so-far / Fog body.
- **Child ticket**: `backlog task create ... -p <map-id> -l wayfinder:<type>` (`research`/`prototype`/`grilling`/`task`).
- **Blocking**: `--dep <id>`; a ticket is unblocked when every dependency is `Done`.
- **Frontier**: `backlog task list -p <map-id> -s "To Do" --ready --plain`; first by id wins.
- **Claim**: `backlog task edit <id> -s "In Progress" -a @me` before any work.
- **Resolve**: `--append-notes "<answer>"`, `-s "Done"`, then append a pointer (gist + id) to the map's Decisions-so-far with `--append-notes` on the map.
