# Editions scope talks and agenda; one Current Edition drives the backend

## Status

accepted

## Context

Jelatyna had no notion of a conference year. Preparing a new edition meant three manual
steps:

- changing the hardcoded dump folder (`edition-2026`) in the backend and redeploying
- changing the C4P window in env vars (`CONFERENCE_C4P_START/END`)
- deleting the previous year's talks, votes and rates from the database

The deletion lost every speaker's history. It also happened to be the only thing that
ever removed voters' IP addresses (`vote.client`). On 2026-10-21 prod held only 2026 data:
118 talks, 36,817 votes and 133 rates. Past years' webpages survive as separate sites
that read their own frozen dump folders.

## Decision

- **An Edition** has a year (unique, fixed once created; its dump folder is
  `edition-<year>`), a name, conference dates (start date and time, last day) and an
  optional C4P period (unset means closed). It owns its talks and workshops, and through
  them their votes, rates and likes, and its agenda (days, rooms, time slots). Users,
  FAQ, partners, pages and News Entries stay global: a past year's view of them is its
  Archive, the dumps that froze when the next Edition became current.
- **Exactly one Edition is current, and it alone drives the backend:**
  - where dumps are written, including a new `edition.json` (year, name, dates) that
    the webpage countdown and banner dates read
  - where talks are submitted, while its C4P is open (admins can always submit)
  - which talks voting and rating use
  - what admin pages and the speaker homepage show

  Rejected: two switches (one for publishing, one for submissions). That would allow
  next year's C4P while the webpage still shows this year, but it is two things to keep
  in sync for a once-a-year step.
- **Making an Edition current is a manual admin action** with a confirmation. Switching
  back is allowed. Rejected: switching automatically on a date, because a mistyped date
  would switch early and trigger the irreversible step below.
- **When an Edition stops being current, its votes' IP addresses are cleared.** IPs only
  matter while voting is open. Nothing else of a past Edition is deleted.
- **The webpage does not follow the Current Edition.** It keeps its dump folder in its
  own config, so after a switch it shows the frozen previous Archive until its config is
  bumped along with the rest of the yearly webpage checklist (`webpage/NEXT-EDITION.md`).
  Rejected: a `current/` folder the webpage reads automatically. It would show an empty
  new year beside last year's hardcoded banner the moment the backend switches.

## Consequences

- The previous Archive freezes at the switch. Change admins and volunteers for the new
  year after switching, or the past Archive loses the people who organised it.
- Admin pages show only the Current Edition. Looking at past years in the admin app is
  left for later.
- Past Editions' talks are read-only for speakers, who see them through an Edition picker
  that appears only when they have talks outside the Current Edition.
- A migration creates Edition 2026 as current, with prod's C4P period at the time, and
  assigns it every existing talk and the agenda. The C4P env vars and properties are
  removed.
