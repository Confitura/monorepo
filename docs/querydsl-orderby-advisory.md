# QueryDSL `orderBy` HQL-injection advisory — assessment

Dependabot alerts **#880** (`com.querydsl:querydsl-jpa`) and **#881**
(`com.querydsl:querydsl-apt`), both **high** severity:

> Querydsl vulnerable to HQL injection through `orderBy`
> (GHSA affects `<= 5.1.0`; **no patched version exists** — 5.1.0 is the latest release).

## Verdict: not exploitable in this codebase — keep, do not bump

There is no version to upgrade to, so the alert cannot be resolved by a bump.
It also does not apply to us, because the vulnerability requires an
**attacker-controlled string** to reach `orderBy`.

Every `orderBy` call in the backend uses a **statically compiled** QueryDSL
path expression, never a user-supplied value. As of this writing all usages are
in `DashboardController`:

```java
.orderBy(participationData.arrivalDate.asc())   // registrations-over-time
.orderBy(participationData.createdDate.asc())   // registrations
.orderBy(vote.voteDate.asc())                   // votes
```

There is no `Sort`, `Pageable`, `OrderSpecifier` built from request input, or
any other path from an HTTP parameter to an ordering clause anywhere in
`jelatyna-backend/src/main/java`.

## What would change this

Re-assess (and prefer sanitising the input over relying on this note) if anyone
introduces ordering driven by a request parameter — e.g. a `?sort=` query param
mapped to a column name, a `Pageable`/`Sort` argument on a controller, or an
`OrderSpecifier` constructed from a string. In that case, validate the field
against an allowlist of known column expressions before passing it to `orderBy`.

## Recommended handling of the alerts

Dismiss #880 and #881 in the GitHub Dependabot UI as **"Risk is tolerable /
not affected"**, referencing this document. There is no upstream fix to wait
for; the QueryDSL project's maintained continuation is the
`io.github.openfeign.querydsl` fork, which could be evaluated separately if a
patched release becomes available.
