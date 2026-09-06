import 'dotenv/config'
import { DatalinksClient } from './datalinks'
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

function required(name: string): string {
  const v = process.env[name]
  if (!v) throw new Error(`Missing required env var: ${name}`)
  return v
}

const DATASETS = {
  talks: 'talks',
  pages: 'pages',
  agenda: 'agenda',
  sponsors: 'sponsors',
  faq: 'faq',
  news: 'news',
} as const

async function main() {
  const resourcesBaseUrl = required('RESOURCES_BASE_URL')
  const client = new DatalinksClient({
    baseUrl: required('DATALINKS_BASE_URL'),
    token: required('DATALINKS_TOKEN'),
    username: required('DATALINKS_USERNAME'),
    namespace: required('DATALINKS_NAMESPACE'),
  })

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

  const talkRows = toTalkRows([...presentations, ...workshops])
  const pageRows = toPageRows(pages)
  const agendaRows = toAgendaRows(days)
  const sponsorRows = toSponsorRows(sponsors)
  const faqRows = toFaqRows(faqEntries)
  const newsRows = toNewsRows(news)

  await ingestDataset(client, DATASETS.talks, talkRows, 'Confitura conference talks and workshops (speakers referenced by opaque id only). Each row has a `url` to its page.')
  await ingestDataset(client, DATASETS.agenda, agendaRows, 'Confitura conference schedule: which talk/workshop is in which room at which time, per day. `url` links to the schedule.')
  await ingestDataset(client, DATASETS.sponsors, sponsorRows, 'Confitura sponsors/partners: name, tier, description, website and partner-page `url`.')
  await ingestDataset(client, DATASETS.faq, faqRows, 'Confitura FAQ: question/answer grouped by category (registration, tickets, venue, conference day, …). `url` links to the FAQ page.')
  await ingestDataset(client, DATASETS.pages, pageRows, 'Confitura info pages (venue, about, …) as markdown, each with its `url`.')
  await ingestDataset(client, DATASETS.news, newsRows, 'Confitura news/announcements with publish dates and a `url` to the news page.')

  console.log('Done.')
}

async function ingestDataset(
  client: DatalinksClient,
  name: string,
  rows: object[],
  description: string,
) {
  if (rows.length === 0) {
    console.log(`- ${name}: no rows, skipping`)
    return
  }
  console.log(`- ${name}: refreshing (${rows.length} rows)…`)
  await client.clearDataset(name)
  await client.createDataset(name, description).catch(() => {
    /* already exists — ingest still works */
  })
  const indexed = await client.ingest(name, rows, description)
  console.log(`  ${name}: indexed ${indexed} rows`)
}

main().catch((err) => {
  console.error(err instanceof Error ? err.message : err)
  process.exit(1)
})
