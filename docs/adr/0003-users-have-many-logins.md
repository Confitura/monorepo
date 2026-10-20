# Users have many Logins; merging proves both with two fresh tokens

## Status

accepted

## Context

Every identity-provider sign-in created its own `User`. `OAuthUserService` looked a person
up by `users.social_id` (base64 of `provider/id`) and created a new User when none
matched, so someone who signed in with GitHub and later with Google got two unrelated
Users, each with its own roles, talks and profile. In prod (2026-10-20) 11 email addresses
were shared by Users from different providers. Admins also create Manual Users (no
sign-in yet, e.g. speakers), which can only be joined to the person's later sign-in by
hand.

The provider redirects back to `/login/{provider}/callback` without an `Authorization`
header, so the callback cannot tell who is already signed in. The admin app sent a fixed
`state=static-state`, which left sign-in open to login CSRF.

## Decision

- **A User has zero or more Logins.** A Login is `provider` + `external_id` (decoded, not
  base64), unique, owned by exactly one User. A Manual User is a User with no Login. The
  `login` table is introduced in two steps: one migration creates and backfills it while
  `users.social_id` and `users.origin` stay (so the previous image can still be
  redeployed); a later one drops them.
- **Link, merge and unlink.** A signed-in User links a new Login by completing that
  provider's sign-in. If the Login already belongs to another User, the two Users merge:
  self-service keeps the signed-in User, an admin merge (the only way for Manual Users)
  lets the admin pick the survivor. The survivor keeps its own profile values and takes
  the absorbed User's only where its own are empty; roles and privacy consent combine;
  talks, Logins and the ticket move; two tickets block the merge; the absorbed User is
  deleted. Merges are irreversible. A User can unlink any Login but their last; an admin
  can unlink any.
- **Two tokens prove both Users.** The OAuth callback is unchanged: the second sign-in
  goes through the normal flow and yields a second JWT (for a never-seen Login, the normal
  flow creates an empty User). `POST /users/merge` takes the current JWT as the survivor
  and the second JWT as proof of the absorbed User, which must be freshly issued (`iat`
  within minutes), so an old or leaked token can't be used. Carrying a signed link intent
  through the OAuth `state` was rejected: it would branch the security-sensitive callback
  and add two token types to avoid one short-lived User row.
- **Forged sign-ins are blocked.** The front end sends a random `state` per sign-in
  attempt, stores it with the intent (sign in vs. add login) in `sessionStorage`, and
  accepts a returned token only when both match. The merge confirmation is always shown,
  even when the other User is brand new and empty; cancelling deletes such a User.
- **Tokens follow the merge.** The survivor gets a freshly issued JWT, since its roles may
  have changed. Requests with a token whose User no longer exists get `401`; no alias
  from absorbed to survivor ids is kept.
- No automatic duplicate detection or auto-merge by email: email ownership isn't verified
  the same way by every provider. Admins merge when a duplicate is reported. Each merge,
  link and unlink logs one structured line with ids only.

## Consequences

- Every authenticated request now checks that its User exists. That's one cheap lookup,
  which also covers Users an admin deletes.
- A brand-new Login's User exists briefly between the second sign-in and the merge
  confirmation; the confirmation must delete it on cancel.
- A merge can break a public speaker URL (`/speakers/{id}`) when the absorbed User was the
  published speaker; in an admin merge the admin avoids it by keeping that User as the
  survivor.
- Twitter Logins stay keyed by username, as before, so a Twitter rename still produces a
  new Login.
