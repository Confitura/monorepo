import type { Presentation, Page, AgendaDay } from './types'

// Reads the public Confitura resource JSON (unauthenticated, static files).

async function getJson<T>(baseUrl: string, path: string): Promise<T> {
  const url = `${baseUrl.replace(/\/$/, '')}/${path.replace(/^\//, '')}`
  const res = await fetch(url)
  if (!res.ok) throw new Error(`GET ${url} -> ${res.status} ${res.statusText}`)
  return (await res.json()) as T
}

export async function fetchPresentations(baseUrl: string): Promise<Presentation[]> {
  return getJson<Presentation[]>(baseUrl, 'presentations/accepted.json')
}

export async function fetchWorkshops(baseUrl: string): Promise<Presentation[]> {
  return getJson<Presentation[]>(baseUrl, 'workshops/accepted.json')
}

// CMS pages are stored as a JSON-encoded markdown string, e.g. "## Registration…".
// Returns null for pages that aren't published (404) so ingest can skip them.
export async function fetchPage(baseUrl: string, slug: string): Promise<Page | null> {
  const url = `${baseUrl.replace(/\/$/, '')}/pages/${slug}.json`
  const res = await fetch(url)
  if (res.status === 404) return null
  if (!res.ok) throw new Error(`GET ${url} -> ${res.status} ${res.statusText}`)
  return { slug, content: (await res.json()) as string }
}

interface RawAgenda {
  timeSlots: { displayOrder: number; label: string }[]
  rooms: { id: string; label: string; displayOrder: number }[]
  presentations: Presentation[]
  agendaEntries: {
    timeSlotIndex: number
    roomId: string
    roomLabel: string
    presentationId: string | null
    timeSlotLabel: string
  }[]
}

export async function fetchAgendaDay(baseUrl: string, dayId: string): Promise<AgendaDay> {
  const raw = await getJson<RawAgenda>(baseUrl, `agenda/${dayId}.json`)
  return {
    dayId,
    // Build lookups the transform expects straight from the entries so labels
    // always resolve even if timeSlots/rooms drift.
    timeSlots: raw.agendaEntries.map((e) => ({ index: e.timeSlotIndex, label: e.timeSlotLabel })),
    rooms: raw.agendaEntries.map((e) => ({ id: e.roomId, label: e.roomLabel, displayOrder: 0 })),
    presentations: raw.presentations,
    entries: raw.agendaEntries.map((e) => ({
      timeSlotIndex: e.timeSlotIndex,
      roomId: e.roomId,
      presentationId: e.presentationId,
    })),
  }
}
