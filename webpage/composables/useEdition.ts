import { useArchiveFetch } from '~/composables/useAPIFetch'

// The Current Edition as the backend publishes it in edition.json.
// `data` stays empty while that file is missing, so callers hide what depends on it.
export interface PublishedEdition {
  year: number
  name: string
  conferenceStart: string
  lastDay: string | null
}

export const useEdition = () => useArchiveFetch<PublishedEdition>('/edition.json', { key: 'edition' }).data
