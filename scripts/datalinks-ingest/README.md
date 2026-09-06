# datalinks-ingest

Manual MVP script that loads Confitura conference resource data into a Datalinks
namespace so the public chat assistant can answer questions grounded in it.

## Privacy boundary (GDPR)

Speaker **name, photo and bio are never sent to Datalinks**. Talks are ingested linked to
an **opaque speaker id only**; the backend rejoins ids ↔ names at the edge. Abstracts are
ingested as-is (not scrubbed), so a name inside an abstract may still reach Datalinks — the
guarantee is "we don't deliberately send name/bio/photo/email fields." This boundary is
enforced by `test/transform.test.ts`.

## Datasets created

Every row carries a relative `url` to its page on the site (e.g. `/presentations#<id>`,
`/partners/<slug>`, `/schedule/<dayId>`) so the assistant can link to it.

- `talks` — accepted talks + workshops (title, abstract, level, language, tags, duration, speakerIds, url)
- `agenda` — talk/workshop → day/time/room placement (`AGENDA_DAYS`, default `day-1,day-2`)
- `sponsors` — partners (name, tier, description, website, url) — companies, no PII
- `faq` — structured FAQ entries (category, question, answer, url) from `/faq/entries.json`
- `pages` — info pages (venue, about, …) as markdown, with url (`RESOURCES_PAGES`)
- `news` — announcements (title, body, date, url)

The backend also sends Datalinks a `helperPrompt` (see `ChatConfigurationProperties`) that
keeps answers on-topic, replies in the visitor's language, and links to each record's `url`.

## Usage

```bash
pnpm install
cp .env.example .env    # fill in DATALINKS_TOKEN, DATALINKS_USERNAME
pnpm ingest             # fetch resources, transform, clear-then-reingest
pnpm test               # run the transform / PII-boundary tests
pnpm typecheck
```

Re-running `pnpm ingest` refreshes the data (clear-then-reingest).
