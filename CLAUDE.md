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
./mvnw spring-boot:run                        # Run dev server (add -Pfake-db,fake-security for local dev)
./mvnw test                                   # Run all tests
./mvnw test -Dtest=MyTest                     # Run a single test class
./mvnw package                                # Build JAR
```

**Development profiles:**
- `fake-db` — **in tests** uses H2 in-memory (`src/test/resources/application-fake-db.yml`). The **main** `fake-db` profile (`src/main/resources/application-fake-db.yml`) points at a local **PostgreSQL** on `localhost:5432` (db/user `jelatyna`, pass `password`) with `ddl-auto: create-drop`. H2 is a **test-scoped** dependency, so `spring-boot:run` cannot use it.
- `fake-security` — enables fake OAuth users (Google admin, Facebook volunteer)

Run locally with both: `./mvnw spring-boot:run -Dspring-boot.run.profiles=fake-db,fake-security` — this needs a Postgres running, e.g. `docker run -d --name pg -e POSTGRES_DB=jelatyna -e POSTGRES_USER=jelatyna -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:16`. (Or override the datasource to H2 via `SPRING_DATASOURCE_URL`, but H2 isn't on the runtime classpath by default.)

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
- **Database** — JPA/Hibernate with QueryDSL; MySQL (prod) or H2 (dev)
- **Fake users** — `FakeLoginUsers` and `application-fake-db.yml` allow local development without real OAuth credentials

OpenAPI/Swagger UI available at `/api/swagger-ui.html` when running.

### Frontend

Both frontends communicate with the backend REST API. They use file-based routing and auto-imports.

- **admin-app** — Vite + Vue 3 + Vuetify + Pinia + Axios. Routing via `unplugin-vue-router`.
- **webpage** — Nuxt 3 + Pinia + `vue3-google-map` + Plausible/Umami analytics. SSR capable with `nuxt generate` for static export.

### Local Infrastructure

Docker Compose (`docker-compose.yml`) provides MySQL (3306), backend (8080), and nginx. For frontend-only work, running the backend locally with fake profiles is simpler.

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

Data lands via the scheduled `WebpageDataDumper.dumpAll()` (~10 min) into the resource feed the webpage reads (`api.confitura.pl/api/resources/edition-2026/…`). No DB migration is needed for new columns — prod runs `ddl-auto: update`.

## Agent skills

### Issue tracker

Local, private Backlog.md board in `backlog/` (gitignored). See `docs/agents/issue-tracker.md`.

### Triage labels

The default five roles (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Code factory

`/factory` (`.claude/skills/factory/SKILL.md`) works the Backlog.md board **one task at a time**: `run [task-id]` takes the next `ready-for-agent` task whose dependencies are `Done`, branches `factory/<id>-<slug>` off `origin/master`, has the `factory-implementer` subagent build it, the separate `factory-reviewer` subagent check it, and opens a **draft** PR (task → `In Review`). A human reviews and merges; `sync` marks merged tasks `Done`. `setup` creates the board once. Watch it with `backlog browser` (http://localhost:6420).

Factory subagents never start dev servers, e2e, or docker (except containers Testcontainers starts inside `./mvnw test`), never run `backlog`/`git push`/`gh`, and run only the checks for the modules they touched. `.claude/settings.json` denies `gh pr merge`, `gh pr ready`, force pushes, and pushes to `master`.
