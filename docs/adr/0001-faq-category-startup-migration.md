# FAQ categories: startup-time backfill migration on ddl-auto:update

## Status

accepted

## Context

FAQ entries stored their category as a free-text `String`. We are promoting the
category to a first-class `FaqCategory` dictionary entity (own `published` flag and
`displayOrder`), with `FaqEntry` referencing it by foreign key. The public dump
(`/faq/entries.json`) must keep its exact schema — a flat list where each entry carries
`category` as a plain string — so no consumer (webpage) changes.

The backend has **no migration tool** (no Flyway/Liquibase) and prod runs Hibernate
`ddl-auto: update`. Schema changes therefore come from Hibernate, and any data backfill
has to be application code.

## Decision

Run the value→entity backfill as a startup `CommandLineRunner` in its own transaction:
read the distinct existing `category` strings via a native query, create one
`FaqCategory` per distinct value (`published = true`, `displayOrder` assigned
**alphabetically** so the pre-migration public order is reproduced exactly), and point
each `FaqEntry` at its category. The runner is idempotent — it skips entirely when any
`FaqCategory` already exists — so it is safe to leave in the codebase across all future
deploys.

The old `category` string column is **not dropped** (`ddl-auto: update` never drops
columns); it is left orphaned in the DB and removed from the entity, so the migration
reads it via native SQL rather than through JPA.

## Consequences

- No manual migration step at deploy time; the first boot after deploy performs the
  cutover automatically, later boots are no-ops.
- A dead `category` column lingers in prod until someone drops it by hand.
- Introducing Flyway later remains possible but is explicitly out of scope here — the
  one-shot backfill did not justify the tooling.
