import { computed } from 'vue'
import { useArchiveFetch } from '~/composables/useAPIFetch'

interface DayAgendaEntry {
  presentationId: string | null
}

interface DayAgenda {
  agendaEntries?: DayAgendaEntry[]
}

// The four published agenda feeds (presentations + workshops, both days). A
// presentation is "scheduled" if it appears in any of them. Missing/unpublished
// feeds resolve to null and simply contribute nothing, so before the schedule is
// published the set is empty and no calendar icons show.
const DAY_FEEDS = ['day-1', 'day-2', 'day-1-workshops', 'day-2-workshops']

export function useScheduledPresentationIds() {
  const feeds = DAY_FEEDS.map(dayId =>
      useArchiveFetch(`/agenda/${dayId}.json`, { key: `scheduled-agenda-${dayId}` }).data
  )

  return computed(() => {
    const ids = new Set<string>()
    for (const feed of feeds) {
      const entries = (feed.value as DayAgenda | null)?.agendaEntries || []
      for (const entry of entries) {
        if (entry.presentationId) {
          ids.add(entry.presentationId)
        }
      }
    }
    return ids
  })
}
