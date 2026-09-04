import 'dotenv/config'
import { DatalinksClient } from './datalinks'
import { fetchPresentations, fetchWorkshops, fetchPage, fetchAgendaDay } from './resources'
import { toTalkRows, toPageRows, toAgendaRows } from './transform'
import type { Page, AgendaDay } from './types'

function required(name: string): string {
  const v = process.env[name]
  if (!v) throw new Error(`Missing required env var: ${name}`)
  return v
}

const DATASETS = { talks: 'talks', pages: 'pages', agenda: 'agenda' } as const

async function main() {
  const resourcesBaseUrl = required('RESOURCES_BASE_URL')
  const client = new DatalinksClient({
    baseUrl: required('DATALINKS_BASE_URL'),
    token: required('DATALINKS_TOKEN'),
    username: required('DATALINKS_USERNAME'),
    namespace: required('DATALINKS_NAMESPACE'),
  })

  const pageSlugs = (process.env.RESOURCES_PAGES ?? 'faq')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
  const agendaDays = (process.env.AGENDA_DAYS ?? '')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)

  console.log('Fetching Confitura resources…')
  const [presentations, workshops] = await Promise.all([
    fetchPresentations(resourcesBaseUrl),
    fetchWorkshops(resourcesBaseUrl),
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

  await ingestDataset(client, DATASETS.talks, talkRows, 'Confitura conference talks and workshops (speakers referenced by opaque id only)')
  await ingestDataset(client, DATASETS.pages, pageRows, 'Confitura conference FAQ and information pages (markdown)')
  if (agendaRows.length > 0) {
    await ingestDataset(client, DATASETS.agenda, agendaRows, 'Confitura conference agenda: which talk is in which room at which time')
  }

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
