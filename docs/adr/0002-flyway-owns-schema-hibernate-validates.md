# Flyway owns the schema; Hibernate only validates

## Status

accepted — supersedes the migration mechanism of ADR-0001

## Context

Hibernate has always created the schema: `ddl-auto: update` in prod, `create-drop` locally
and in tests (H2). `update` never drops anything, so prod has accumulated columns that no
entity maps any more (e.g. `faq_entry.category`, see ADR-0001), and data backfills had to be
written as startup `CommandLineRunner`s. Prod runs PostgreSQL (the only JDBC driver since the
MySQL connector was removed in April 2025).

## Decision

Flyway applies every schema and data change; Hibernate runs with `ddl-auto: validate`
everywhere.

- **Baseline from prod, not from the entities.** `V001__baseline.sql` is
  `pg_dump --schema-only --no-owner --no-privileges` of prod, dead columns included. Prod is
  marked as already at V001 via `baseline-on-migrate` / `baseline-version: 1`, so its first
  Flyway deploy changes nothing but adding `flyway_schema_history`. Generating V001 from the
  entities was rejected: prod would silently differ from every fresh database.
- **`baseline-on-migrate` is temporary.** It is set only in `application-prod.yml` and removed
  once prod has a history table, so a schema that loses its history fails loudly instead of
  being re-baselined and having later (possibly non-idempotent) migrations re-applied.
- **Tests run on Testcontainers PostgreSQL**, not H2. A `pg_dump` baseline is Postgres-only SQL,
  and running migrations plus `validate` on every test run catches entity/migration drift.
  Factory agents may therefore start Docker via Testcontainers inside `./mvnw test`.
- **`fake-db` cleans and migrates on every boot** (a profile-scoped `FlywayMigrationStrategy`;
  `clean-disabled: false` only in the `fake-db` yml). The fake-data seeders insert
  unconditionally and relied on `create-drop`'s empty database.
- **Versions are zero-padded sequential integers** (`V001`, `V002`, …). Two branches picking
  the same number fail loudly on master and are fixed by renaming; timestamps were rejected
  because out-of-order merges either block startup or need `outOfOrder`.

## Consequences

- Every entity change needs a hand-written `V00N__*.sql` in the same PR; the Testcontainers
  suite fails if one is missing.
- Dead columns live in V001 and are dropped by a later migration, not by the baseline.
- Startup `CommandLineRunner` migrations (ADR-0001's pattern) are no longer used;
  `FaqCategoryMigration` is deleted once prod is confirmed migrated.
