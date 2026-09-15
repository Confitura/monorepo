import 'dotenv/config'
import { readFile } from 'node:fs/promises'
import { DatalinksClient } from './datalinks'
import { DATASETS, DATA_FILE, type PreparedDataset } from './datasets'

function required(name: string): string {
  const v = process.env[name]
  if (!v) throw new Error(`Missing required env var: ${name}`)
  return v
}

async function main() {
  const client = new DatalinksClient({
    baseUrl: required('DATALINKS_BASE_URL'),
    token: required('DATALINKS_TOKEN'),
    username: required('DATALINKS_USERNAME'),
    namespace: required('DATALINKS_NAMESPACE'),
  })

  const raw = await readFile(DATA_FILE, 'utf8').catch(() => {
    throw new Error(`No prepared data at ${DATA_FILE}. Run \`pnpm download\` first.`)
  })
  const datasets = JSON.parse(raw) as PreparedDataset[]

  for (const dataset of datasets) {
    await ingestDataset(client, dataset)
  }

  // Manual bidirectional link: agenda.talkId <-> talks.id (no auto discovery/curation).
  console.log('- linking agenda.talkId <-> talks.id…')
  await client.addLink(
    { dataset: DATASETS.agenda, columnName: 'talkId' },
    { dataset: DATASETS.talks, columnName: 'id' },
  )
  await client.addLink(
    { dataset: DATASETS.talks, columnName: 'id' },
    { dataset: DATASETS.agenda, columnName: 'talkId' },
  )

  console.log('Done.')
}

async function ingestDataset(client: DatalinksClient, dataset: PreparedDataset) {
  if (dataset.rows.length === 0) {
    console.log(`- ${dataset.name}: no rows, skipping`)
    return
  }
  console.log(`- ${dataset.name}: refreshing (${dataset.rows.length} rows)…`)
  await client.clearDataset(dataset.name)
  await client.createDataset(dataset.name, dataset.description).catch(() => {
    /* already exists — ingest still works */
  })
  const indexed = await client.ingest(dataset.name, dataset.rows, dataset.description)
  console.log(`  ${dataset.name}: indexed ${indexed} rows`)
}

main().catch((err) => {
  console.error(err instanceof Error ? err.message : err)
  process.exit(1)
})
