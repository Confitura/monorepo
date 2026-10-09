# Preparing the next edition (webpage)

> Last rollover: 2026 → 2027 (September 2026). Values below still quote the 2026 examples;
> the 2027 date/venue were not known at rollover, so `TODO(2027)` markers flag what is pending
> (`TheTimer.vue`, `calendar.vue` → `dateAnnounced`, `admin-app/src/pages/login.vue`).

## 0. Archive the finished edition first

Done once per year, before any of the steps below (see the `archive-2025` / `archive-2026` branches):

1. Branch `archive-<year>` from `master` and push it.
2. On that branch, reduce `.github/workflows/deploy-images.yml` to the **webpage job only**,
   triggered by pushes to `archive-<year>`. Tag the image `confitura-webpage:archive-<year>`
   (never `:latest` — master owns it) and keep the **current** webpage Coolify UUID.
   Backend and admin-app are shared across editions and deploy from `master` only.
3. Add `archive-<year>` to the push branches in `.github/workflows/run-tests.yml` on that branch.
4. In Coolify: point the existing webpage resource at the `:archive-<year>` image and the
   `<year>.confitura.pl` domain; create a new webpage resource on `:latest` / `confitura.pl`.
5. On `master`, put the new resource's UUID in the webpage job of `deploy-images.yml`.
6. Backend: bump `edition-<year>` in `ArchiveConfiguration.java` so the old edition's dump folder
   freezes (the archive keeps reading it). Before the first dump, unpublish/clear last year's
   FAQ, partners and agenda in the admin, or they are republished for the new edition.
7. `components/TheMenu.vue`: link the archived year (`https://<year>.confitura.pl/`).

Checklist of the edition-specific values hardcoded in the webpage. Update these when
rolling over to a new Confitura year. Paths are relative to `webpage/`.

Quick audit any time: `grep -rn "2027\|TODO(2027)\|September 2\|Grzybowska\|edition-2027\|partners/2027" components pages public stores nuxt.config.ts`

## 1. Core event data — date, time & venue

These drive the countdown, the banner, and the "add to calendar" feature. Get them right first.

- **`components/home/MainBanner.vue`**
  - line ~16: banner date — `Warsaw | September 25–26, 2026`
  - line ~17: venue — `ADN Conference Center, Grzybowska 56`
  - line ~47: `let date = dayjs('2025-06-24T09:00')` — stale/legacy date constant; update or remove.
- **`components/TheTimer.vue`** line ~30: countdown target — `dayjs('2026-09-25T09:00:00')`.
- **`components/home/InvitePartners.vue`** line ~9: `Confitura 2026 is coming on September 25–26`.
- **`pages/venue.vue`** line ~32: map center `{lat, lng}` — only if the venue moves.

## 2. Add-to-calendar feature (added for 2026)

If the date or venue changes, all of these must change together, or calendars get wrong data.

- **`public/confitura-2026.ics`** — rename the file to the new year and update inside:
  `UID`, `DTSTART`/`DTEND` (all-day, end date is **exclusive** → day-after-last), `SUMMARY`, `LOCATION`.
- **`pages/calendar.vue`**:
  - summary block (title / `September 25–26, 2026` / venue),
  - `googleUrl` `dates=20260925/20260927` (end exclusive) + `text`,
  - `outlookUrl` `startdt`/`enddt` + `subject`,
  - `location` and `details` consts,
  - `icsFile = '/confitura-2026.ics'` — match the renamed file,
  - `useHead` title/description.
- The **all-presentations feed** (`webcal://api.confitura.pl/api/agenda/ical/subscribe`) is backend-driven; no yearly change here.

## 3. Data feeds & partner assets (`nuxt.config.ts` + partners)

- **`nuxt.config.ts`** lines ~11–12: `apiServer` (`archive/2026/`) and `archiveServer` (`edition-2026/`) — bump the edition path.
- **Partner logo glob** `import.meta.glob('~/assets/partners/2026/*')` in:
  `components/home/Partners.vue`, `pages/partners/index.vue`, `pages/partners/[id].vue`.
- Add the new year's logo folder under **`assets/partners/<year>/`**.
- **`stores/partnersStore.ts`** lines ~119/127/135: fallback logo paths `/assets/partners/2026/…`.

## 4. Archive navigation

- **`components/TheMenu.vue`** line ~35: the `2025` menu link (`http://2025.confitura.pl/`).
  After 2026 wraps, add a `2026` archive entry / bump the year.

## 5. SEO titles & descriptions (mechanical, site-wide)

Every page hardcodes `Confitura 2026` in its `useHead` title/description. Find & replace the year:
`grep -rln "Confitura 2026" pages` → `index`, `faq`, `news`, `tickets`, `terms-and-conditions`,
`venue`, `presentations`, `workshops`, `partners/index`, `partners/[id]`, `speakers/[id]`,
`schedule/[[dayId]]`, `schedule/workshops/[[dayId]]`, plus `pages/calendar.vue`.

## Verify after updating

- `pnpm test` — page render suite still green.
- `pnpm dev` → homepage countdown/date correct; `/calendar` buttons produce an event on the **new** dates; download the `.ics` and open it.
- `pnpm build` → new `.ics` present in `.output/public/`.
