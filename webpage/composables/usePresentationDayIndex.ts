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
export function usePresentationDayIndex() {
  const feeds = DAYS.map(day => ({
    day,
    data: useArchiveFetch(`/agenda/${day}.json`, { key: `day-index-${day}` }).data,
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
