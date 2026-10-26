import { computed } from 'vue'
import { useArchiveFetch } from '~/composables/useAPIFetch'

interface DayAgendaEntry {
  presentationId: string | null
}

interface DayAgenda {
  agendaEntries?: DayAgendaEntry[]
}

const DAYS = ['day-1', 'day-2']

// Maps each scheduled presentation id to the day it is on, across both
// presentation days, so a deep link like /schedule#<id> can open the right day.
// Empty until the agenda feeds load (or if no schedule is published yet).
// The schedule page already fetches its own day in full under `day-agenda-<day>`, so that day
// shares the page's fetch; the other day is only needed for its ids, so only the ids are kept.
export function usePresentationDayIndex() {
  const currentDay = String(useRoute().params.dayId || 'day-1')
  const feeds = DAYS.map(day => ({
    day,
    data: day === currentDay
        ? useArchiveFetch(`/agenda/${day}.json`, { key: `day-agenda-${day}`, dedupe: 'defer' }).data
        : useArchiveFetch(`/agenda/${day}.json`, {
          key: `day-index-${day}`,
          transform: (feed: DayAgenda | null) => ({
            agendaEntries: (feed?.agendaEntries || []).filter(e => e.presentationId).map(({ presentationId }) => ({ presentationId })),
          }),
        }).data,
  }))

  return computed(() => {
    const index = new Map<string, string>()
    for (const { day, data } of feeds) {
      const entries = (data.value as DayAgenda | null)?.agendaEntries || []
      for (const entry of entries) {
        if (entry.presentationId && !index.has(entry.presentationId)) {
          index.set(entry.presentationId, day)
        }
      }
    }
    return index
  })
}
