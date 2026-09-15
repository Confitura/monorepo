// Shared contract between `download` (prepares data) and `ingest` (publishes it).

export const DATASETS = {
  talks: 'talks',
  pages: 'pages',
  agenda: 'agenda',
  sponsors: 'sponsors',
  faq: 'faq',
  news: 'news',
} as const

// A dataset ready to publish: its Datalinks name, the description sent as
// `dataDescription`, and the sanitized rows produced by the transforms.
export interface PreparedDataset {
  name: string
  description: string
  rows: object[]
}

// Where `download` writes and `ingest` reads the prepared datasets.
export const DATA_FILE = 'data/datasets.json'
