# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a monorepo for the **Confitura** conference management system, consisting of three modules:

- **jelatyna-backend** — Spring Boot 3 REST API (Java 21, Maven)
- **admin-app** — Vue 3 + Vuetify admin dashboard (TypeScript, pnpm)
- **webpage** — Nuxt 3 public conference website (TypeScript, pnpm)

## Commands

### jelatyna-backend

```bash
cd jelatyna-backend
./mvnw spring-boot:run                        # Run local dev server (local + fake-db + fake-security by default; wipes and re-seeds the local DB on every start)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local,fake-security  # Run local dev server keeping the local DB between restarts
./mvnw test                                   # Run all tests
./mvnw test -Dtest=MyTest                     # Run a single test class
./mvnw package                                # Build JAR
```

**Development profiles:**
- `local` — the local-dev settings, all dummies (`src/main/resources/application-local.yml`): a local **PostgreSQL** on `localhost:5432` (db/user `jelatyna`, pass `password`), the JWT secret, the resource folder (`/tmp/confitura`), mail and listmonk keys, and the admin-app CORS origin. It neither cleans nor seeds, so data survives restarts. It holds dummy secrets: never activate it in prod.
- `fake-db` — the destructive demo mode, layered on `local` (or on `test` in tests). Flyway **cleans the schema and re-applies every migration on each start** (`FakeDbFlywayConfiguration`), because the fake-data seeder (`FakePresentations`) inserts unconditionally. The seeder also opens the Current Edition's C4P (from a year before the start to a year after), so the fake speaker can add talks; without `fake-db` the C4P period is whatever the database holds. `application-fake-db.yml` holds nothing but `spring.flyway.clean-disabled: false`, so `fake-db` cannot run alone and no other profile can clean. **In tests** (`BaseIntegrationTest` activates `test` + `fake-db`) the database is a **Testcontainers PostgreSQL** (`postgres:16.10`, prod's major version), so `./mvnw test` needs a running Docker; it is a static `@ServiceConnection` container, so one container serves every cached test context, and `application-test.yml` supplies the other settings. Flyway owns the schema (`src/main/resources/db/migration`, ADR-0002) and Hibernate only validates it (`ddl-auto: validate`) in every profile.
- `fake-security` — enables fake OAuth users (Google admin, Facebook volunteer, GitHub speaker). `FakeLoginUsers` re-saves the three accounts on every start (undoing edits to them) and gives the speaker a test talk and a test workshop only while they have no presentations, so keep-data restarts don't pile up duplicates.

Run locally in one of two modes (note `-P` selects Maven profiles, not Spring ones):
- **Fresh demo data** — a plain `./mvnw spring-boot:run` activates `local`, `fake-db` and `fake-security` (the spring-boot-maven-plugin `<profiles>` in `pom.xml`). **It wipes and re-seeds the local database on every start.**
- **Keep data** — `./mvnw spring-boot:run -Dspring-boot.run.profiles=local,fake-security`. `-Dspring-boot.run.profiles=…` replaces the pom's list, so always include `local`.

Both need a Postgres running, e.g. `docker run -d --name pg -e POSTGRES_DB=jelatyna -e POSTGRES_USER=jelatyna -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:16`.

### admin-app

```bash
cd admin-app
pnpm dev             # Dev server
pnpm build           # Production build
pnpm test:unit       # Unit tests (Vitest)
pnpm test:e2e        # E2E tests (Playwright)
pnpm typecheck       # Type check
pnpm lint            # ESLint
pnpm format          # Prettier
```

Run a single unit test: `pnpm test:unit -- path/to/test`

### webpage

```bash
cd webpage
pnpm dev             # Dev server (port 3000)
pnpm build           # Production build
pnpm generate        # Static site generation
pnpm test            # Unit tests (Vitest)
```

## Architecture

### Backend (jelatyna-backend)

Spring Boot app with a layered architecture:

- **Controllers** (`pl.confitura.jelatyna.*`) — REST endpoints under `/api`
- **Security** — OAuth 2.0 login (Google, Facebook, GitHub, Twitter) via ScribeJava; JWT tokens via JJWT
- **Database** — JPA/Hibernate with QueryDSL; PostgreSQL in prod, locally (`local`) and in tests (Testcontainers)
- **Fake users** — `FakeLoginUsers` and `application-local.yml` allow local development without real OAuth credentials

OpenAPI/Swagger UI available at `/api/swagger-ui.html` when running.

### Frontend

Both frontends communicate with the backend REST API. They use file-based routing and auto-imports.

- **admin-app** — Vite + Vue 3 + Vuetify + Pinia + Axios. Routing via `unplugin-vue-router`.
- **webpage** — Nuxt 3 + Pinia + `vue3-google-map` + Plausible/Umami analytics. SSR capable with `nuxt generate` for static export.

### Local Infrastructure

Docker Compose (`docker-compose.yml`) provides PostgreSQL (5432, `postgres:16.10`), backend (8080), and nginx. For frontend-only work, running the backend locally with fake profiles is simpler.

## CI/CD

GitHub Actions (`.github/workflows/run-tests.yml`) runs on pushes to `master` and on PRs. A `changes` job (`dorny/paths-filter`) gates each test job, so a job runs only when its paths change (backend: `jelatyna-backend/**` or `openapi.json`; webpage: `webpage/**`; admin-app: `admin-app/**` or `openapi.json`):

- **test-backend**: JDK 21, `mvn -B package`, then `git diff --exit-code openapi.json` (fails if the regenerated spec drifts from the committed one)
- **test-frontend** (webpage): Node 24, pnpm 10.5.2, `pnpm install --frozen-lockfile → build → test`
- **test-admin-app**: Node 24, pnpm 10.5.2, `pnpm install --frozen-lockfile → codegen + git diff --exit-code src/client (drift check) → typecheck → test:unit`

`.github/workflows/check-builds.yml` runs on the same triggers with its own paths filter (module directories only, not `openapi.json`) and builds the deployable images: Docker builds of webpage and admin-app, a nixpacks build of the backend.

Each workflow ends in an aggregator job — `all-tests-passed` and `all-builds-passed` — that fails if any job failed and passes if they passed or were skipped. These two are the required checks on `master`.

## Pull requests

A PR description must stand on its own: the chat or Backlog.md task behind it will be forgotten or deleted, so every "why is it done this way?" has to be answerable from the PR alone. Applies to every PR — by hand, in a Claude session, or from `/factory`.

1. **TL;DR first** — one to three plain lines saying what changes. No heading, no preamble.
2. **`## Why`** — the reasoning, in your own words:
   - **Problem:** what was wrong or missing, and for whom.
   - **Approach:** what was done and why this way.
   - **Alternatives considered:** each rejected option and why it lost.
   - **Trade-offs / follow-ups:** known limits, risks, and what was left out on purpose.

   Skip a point only when there is genuinely nothing to say.
3. Then whatever helps review: acceptance criteria, verification, screenshots.

The repo is public: explain decisions, never paste private notes, task text, or chat excerpts.

## Observability

Backend instrumentation (structured ECS logging, Prometheus metrics on a separate management port, OTLP traces) is implemented and verified locally but **not deployed**; no Grafana Cloud stack or Alloy container exists yet. `docs/observability.md` records what was verified, the Boot 4.1 gotchas that cost the most time (tracing needs `spring-boot-starter-opentelemetry`; the OTLP endpoint property is `management.opentelemetry.tracing.export.otlp.endpoint`), and what remains. Build the backend with **JDK 21** — JDK 23+ silently disables Lombok/QueryDSL annotation processing.

`docs/pii-in-logs.md` documents which log statements leaked personal data, the S1–S7 fixes applied, and the conventions to follow when adding logging: log entity ids rather than emails, mask an address when no id exists (`MessageInfo.maskedEmail()`), use allowlist `@ToString(onlyExplicitlyIncluded = true)` on anything holding PII, log third-party error envelopes rather than response bodies, and never put credentials in a query string.

## Design system

`docs/design-system/` holds Confitura's brand rules, design tokens and logo files. Read `docs/design-system/brand.md` before UI work in `webpage` or `admin-app`. Values live in `docs/design-system/tokens.json` (and `tokens.css`); `jam` #EE1F46 is the webpage's `$brand`.

## OpenAPI spec & generated admin client

`openapi.json` (repo root) is **generated** by `OpenApiSpecDumpTest` — it is rewritten whenever backend tests run, and it is committed. The admin-app consumes it: `admin-app` has an `@hey-api/openapi-ts` client under `admin-app/src/client/` generated by `pnpm codegen` (reads `../openapi.json`, output `src/client/`).

- **CI enforces that the committed client matches `openapi.json`.** Any PR that changes a controller (hence `openapi.json`) **must also run `cd admin-app && pnpm codegen` and commit `admin-app/src/client/`** — in the *same* PR. Splitting the spec change and the client regen across two PRs makes the spec-only PR fail CI. (Learned the hard way on the Sponsors CMS.)
- **Name controller methods specifically** — the method name becomes the OpenAPI `operationId`, which becomes the SDK function name. Use `getPublishedFaqEntries`/`createPartner`, not generic `get`/`create` (which produce unusable client names like `get`/`create`/`delete_`).
- Work is tracked on a local, private Backlog.md board (`backlog/`, gitignored); `docs/agents/issue-tracker.md` and the `ready-for-agent` label drive spec → tickets → `/factory`.

## Adding a CMS-managed, published entity (playbook)

The FAQ CMS (#563) and Sponsors CMS (#564) established the pattern for content that admins manage and the webpage renders. To add another (mirror `pl.confitura.jelatyna.faq` / `.partner`):

1. **Entity** (uuid2 id; add `published`, and `displayOrder` only if manual ordering is wanted) + **repository** with `@PreAuthorize("@security.isAdmin()")` on `save`/`deleteById`, a public `findPublished…`, and an admin `findAll…`.
2. **Controller** `@RequestMapping("/things")`: `GET` (public, published only), `GET /all` (admin), `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`; DTO (no audit fields) + nullable request record.
3. **Publish**: add `dumpThings()` to `WebpageDataDumper` (writes `/things/….json`) and wire the repository into the `ArchiveConfiguration` dumper-bean constructor. Keep any legacy dump alive during a transition so consumers don't break.
4. **Admin**: a `src/pages/admin/things.vue` (auto-routed via `definePage`) mirroring `admin/faq.vue`/`partners.vue` — list, create/edit dialog, `DialogConfirm` delete; `md-editor-v3` for markdown, `v-file-input` + the existing `POST /resources/…` for image upload. Regenerate the client (see above).
5. **Webpage**: fetch `/things/….json` via `useArchiveFetch`, render with `marked`; fall back to any prior hardcoded data while the dump is empty.
6. **Tests**: a `*ControllerTest` mirroring `FaqEntryControllerTest`/`PartnerControllerTest`; admin render + interaction spec mirroring `faq.spec.ts`/`partners.spec.ts`; a webpage assertion in `pages.nuxt.test.ts`.

Data lands via the scheduled `WebpageDataDumper.dumpAll()` (~10 min) into the resource feed the webpage reads (`api.confitura.pl/api/resources/edition-2026/…`). Every schema change (new table, column, index, constraint) needs a new Flyway migration in `jelatyna-backend/src/main/resources/db/migration` **in the same PR**, named with the next zero-padded sequential version: `V00N__add_things.sql` (N = the next free number). Never edit a migration that is already on master. Hibernate only validates (`ddl-auto: validate`), so the Testcontainers suite fails if the migration is missing.

## Agent skills

### Issue tracker

Local, private Backlog.md board in `backlog/` (gitignored). See `docs/agents/issue-tracker.md`.

### Triage labels

The default five roles (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Code factory

`/factory` (`.claude/skills/factory/SKILL.md`) works the Backlog.md board **one task at a time**: `run [task-id]` takes the next `ready-for-agent` task whose dependencies are `Done`, branches `factory/<id>-<slug>` off `origin/master`, has the `factory-implementer` subagent build it, the separate `factory-reviewer` subagent check it, and only on a reviewer PASS opens a PR **ready for review** (task → `In Review`). A human still reviews and merges; `sync` marks merged tasks `Done`. `setup` creates the board once. Watch it with `backlog browser` (http://localhost:6420).

To run it unattended, start a Claude Code session in a **separate clone** of the repo (its `backlog/` symlinked to the main checkout's board and listed in that clone's `.git/info/exclude`) and run `/loop /factory auto`. Each iteration syncs, runs at most one ready task, and sends a push notification only when a human is needed: a PR to review, a task handed over, a task split, failing checks or a conflict, an interrupted task, an unblocked human task, or nothing left until PRs are merged.

Factory PRs stay around **300 changed lines** (added plus deleted), hand-written only: `openapi.json`, `admin-app/src/client/**`, lockfiles, and verbatim schema dumps such as the Flyway baseline don't count. The maintainer prefers several small PRs over one big one, so cut tickets (`/to-tickets`) into slices that each fit and leave `master` green and wired. The lead splits a task that clearly won't fit into sequential, dependent tasks before branching; the implementer stops at a green checkpoint if it runs over and the remainder becomes a follow-up task; the reviewer fails an over-budget PR that gives no reason it couldn't be split (one migration plus its entity change is such a reason). The count goes in the report and the PR's Verification section.

Factory subagents never start dev servers, e2e, or docker (except containers Testcontainers starts inside `./mvnw test`), never run `backlog`/`git push`/`gh`, and run only the checks for the modules they touched. `.claude/settings.json` denies `gh pr merge`, `gh pr ready`, force pushes, and pushes to `master`.
