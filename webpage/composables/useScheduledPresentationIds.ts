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
  // Each feed lists every accepted talk in full; keep only the ids so the page payload stays small.
  const feeds = DAY_FEEDS.map(dayId =>
      useArchiveFetch(`/agenda/${dayId}.json`, {
        key: `scheduled-agenda-${dayId}`,
        transform: (feed: DayAgenda | null) => (feed?.agendaEntries || []).flatMap(entry => entry.presentationId ? [entry.presentationId] : []),
      }).data
  )

  return computed(() => {
    const ids = new Set<string>()
    for (const feed of feeds) {
      for (const id of feed.value || []) {
        ids.add(id)
      }
    }
    return ids
  })
}
