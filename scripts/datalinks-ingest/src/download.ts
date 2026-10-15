import 'dotenv/config'
import { writeFile, mkdir } from 'node:fs/promises'
import { dirname } from 'node:path'
import {
  fetchPresentations,
  fetchWorkshops,
  fetchPage,
  fetchAgendaDay,
  fetchSponsors,
  fetchFaqEntries,
  fetchNews,
} from './resources'
import {
  toTalkRows,
  toPageRows,
  toAgendaRows,
  toSponsorRows,
  toFaqRows,
  toNewsRows,
} from './transform'
import type { Page, AgendaDay } from './types'
import { DATASETS, DATA_FILE, type PreparedDataset } from './datasets'

function required(name: string): string {
  const v = process.env[name]
  if (!v) throw new Error(`Missing required env var: ${name}`)
  return v
}

async function main() {
  const resourcesBaseUrl = required('RESOURCES_BASE_URL')

  // FAQ now comes from the structured /faq/entries.json (own dataset), so it is
  // no longer one of the CMS pages ingested here.
  const pageSlugs = (process.env.RESOURCES_PAGES ?? 'venue,about,spoina,privacy-policy')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
  const agendaDays = (process.env.AGENDA_DAYS ?? 'day-1,day-2')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)

  console.log('Fetching Confitura resources…')
  const [presentations, workshops, sponsors, faqEntries, news] = await Promise.all([
    fetchPresentations(resourcesBaseUrl),
    fetchWorkshops(resourcesBaseUrl),
    fetchSponsors(resourcesBaseUrl),
    fetchFaqEntries(resourcesBaseUrl),
    fetchNews(resourcesBaseUrl),
  ])
  const fetchedPages = await Promise.all(pageSlugs.map((s) => fetchPage(resourcesBaseUrl, s)))
  const pages: Page[] = fetchedPages.filter((p): p is Page => p !== null)
  const missing = pageSlugs.filter((_, i) => fetchedPages[i] === null)
  if (missing.length > 0) console.log(`  (skipping unpublished pages: ${missing.join(', ')})`)
  const days: AgendaDay[] = await Promise.all(
    agendaDays.map((d) => fetchAgendaDay(resourcesBaseUrl, d)),
  )

  const datasets: PreparedDataset[] = [
    {
      name: DATASETS.talks,
      description:
        'Confitura conference talks and workshops (speakers referenced by opaque id only). Each row has a `url` to its page.',
      rows: toTalkRows([...presentations, ...workshops]),
    },
    {
      name: DATASETS.agenda,
      description:
        'Confitura conference schedule: which talk/workshop is in which room at which time, per day. `url` links to the schedule.',
      rows: toAgendaRows(days),
    },
    {
      name: DATASETS.sponsors,
      description:
        'Confitura sponsors/partners: name, tier, description, website and partner-page `url`.',
      rows: toSponsorRows(sponsors),
    },
    {
      name: DATASETS.faq,
      description:
        'Confitura FAQ: question/answer grouped by category (registration, tickets, venue, conference day, …). `url` links to the FAQ page.',
      rows: toFaqRows(faqEntries),
    },
    {
      name: DATASETS.pages,
      description: 'Confitura info pages (venue, about, …) as markdown, each with its `url`.',
      rows: toPageRows(pages),
    },
    {
      name: DATASETS.news,
      description:
        'Confitura news/announcements with publish dates and a `url` to the news page.',
      rows: toNewsRows(news),
    },
  ]

  await mkdir(dirname(DATA_FILE), { recursive: true })
  await writeFile(DATA_FILE, JSON.stringify(datasets, null, 2))
  console.log(`Wrote ${datasets.length} datasets to ${DATA_FILE}:`)
  for (const d of datasets) console.log(`- ${d.name}: ${d.rows.length} rows`)
}

main().catch((err) => {
  console.error(err instanceof Error ? err.message : err)
  process.exit(1)
})
