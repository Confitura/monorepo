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
- `fake-db` — uses H2 in-memory DB
- `fake-security` — enables fake OAuth users (Google admin, Facebook volunteer)

Run locally with both: `./mvnw spring-boot:run -Dspring-boot.run.profiles=fake-db,fake-security`

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

GitHub Actions (`.github/workflows/run-tests.yml`) runs on pushes to `master` and on PRs:

- **test-backend**: JDK 21, `mvn package`, Surefire reports
- **test-frontend**: Node 20, `npm ci → lint → build → test` (webpage)
- **test-admin-app**: Node 22, pnpm 10.5.2, `typecheck → test:unit`
