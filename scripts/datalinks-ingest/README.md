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

- `talks` — accepted talks + workshops (title, abstract, level, language, tags, duration, speakerIds)
- `pages` — CMS pages (faq, venue, tickets, …) as markdown
- `agenda` — talk → day/time/room placement (optional; set `AGENDA_DAYS`)

## Usage

```bash
pnpm install
cp .env.example .env    # fill in DATALINKS_TOKEN, DATALINKS_USERNAME
pnpm ingest             # fetch resources, transform, clear-then-reingest
pnpm test               # run the transform / PII-boundary tests
pnpm typecheck
```

Re-running `pnpm ingest` refreshes the data (clear-then-reingest).
