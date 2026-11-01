# Backend packages by area, feature and layer; ArchUnit ratchets the move

## Status

accepted; the migration is done and every rule is strict

## Context

`jelatyna-backend` keeps its code in about twenty flat packages directly under
`pl.confitura.jelatyna` (`agenda`, `presentation`, `user`, `login`, `faq`, `mail`,
`infrastructure`, ...). Each mixes controllers, DTOs, entities, repositories and the
clients of external systems (Mandrill, Listmonk, Datalinks, the OAuth providers through
ScribeJava). Nothing limits which package uses which: `user.User` maps its talks and
`presentation.Presentation` maps its speakers, and `infrastructure` holds security,
persistence, the webpage dumps and the fake-data seeding side by side. A package doesn't
tell a reader what its classes may depend on, and the tangle grows with every feature.

## Decision

**Layout: `pl.confitura.jelatyna.<area>.<feature>.<layer>`.**

- Three areas hold the features:
  - `conference`: edition, presentation, rating, agenda, voting, registration, dashboard
  - `webpage`: faq, partner, page, news, archive, chat
  - `identity`: user, login
- Two more top-level packages: `demo` (the demo data, today's `infrastructure.fakedb`)
  and `platform` (cross-cutting: security, persistence, web, storage).
- Inside a feature: `domain` (JPA entities, Spring Data repositories, domain services,
  port interfaces), `web` (controllers, DTOs, requests), and one package per external
  system it talks to (`listmonk`, `mandrill`, `datalinks`, `google`, ...) implementing a
  domain port. Every feature has `domain` and `web`, even a small one. An adapter that
  two features use lives in `platform.<system>`.
- The domain is pragmatic: it may use `jakarta.persistence` and `org.springframework.data`,
  but not `org.springframework.web`, HTTP clients, ScribeJava or the Mandrill client.
  `@PreAuthorize` stays on repositories.

**Rules.**

- Areas depend one way: `webpage` → `conference` → `identity`, and `webpage` →
  `identity`; never back. `platform` uses no area. `demo` may use anything; nothing uses
  `demo`.
- Across areas an entity holds another area's entity only in the allowed direction;
  otherwise it holds the id. A `Presentation` keeps its `User` speakers, but a `User`
  can no longer map its talks.
- Within an area the features form no cycles, and a feature uses another feature's
  `domain` only, never its `web` or adapter packages.

**Enforcement.** `ArchitectureTest` (ArchUnit) encodes the rules, plus one more: every
class except `JelatynaApplication` lives under `conference`, `webpage`, `identity`,
`demo` or `platform`. During the migration every rule was a `FreezingArchRule` with a
committed violation store (`jelatyna-backend/archunit_store`): a new violation failed the
build; a fixed one dropped out of the store, and the PR that fixed it committed the
smaller store. The area, layer and cycle rules looked only at classes already in the new
packages, so code in the old ones was unchecked until it moved, and moved code had to
follow every rule at once. The "lives in an area" rule listed every class still to move:
it was the migration's progress meter. Its store entry is empty now, so the store and the
freezing are gone: the rules are plain ArchUnit rules, and any violation fails the build.

**Migration: one stack of 17 PRs.** Part 1 adds the test, the store and this ADR and
moves no class. Each later part moves one slice of classes into place and lands on top
of the previous part's open PR (at most four open at a time), so the parts merge one
after another. Moves rewrite imports across the codebase, so parts built side by side
would keep conflicting with each other; a single stack keeps each part small and
reviewable.

**Rejected alternatives.**

- **Layers first at the top level** (`web`, `domain`, `persistence`, each split by
  feature): spreads one feature over three or four trees, so no package shows a feature
  whole and every change to one touches all of them.
- **A strict domain** with persistence models separate from the domain objects and
  mappers between them: about three extra classes per entity, in an app that is mostly
  CRUD over its entities. The pragmatic domain keeps JPA on the entities and keeps only
  web and external clients out.
- **Spring Modulith**: its verification has no ratchet for known violations, so the whole
  tree would have to comply before it could be switched on, and nested modules
  (`area.feature`) need extra configuration.
- **One combined `identity` + `conference` area**: it keeps `User` and talks tangled,
  since nothing would force a direction between them.

## Consequences

- Until the last part merges, two layouts coexist; the old packages are held only by the
  "lives in an area" rule.
- Since the migration is done there is no store to commit: a violation can't be frozen,
  only fixed.
- ArchUnit has to read the class files of the JDK the backend builds with; a JDK upgrade
  may need a newer ArchUnit.
