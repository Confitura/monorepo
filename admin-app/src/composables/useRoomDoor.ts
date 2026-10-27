import { onMounted, onUnmounted, ref } from 'vue'
import {
  getAgendaEntriesByDay,
  getAllDays,
  getAllPresentations,
  getAllRooms1,
  getAllTimeSlots,
  type FullPresentation,
  type InlineAgendaEntry,
  type InlineRoom,
  type InlineTimeSlot,
} from '@/utils/api.ts'
import { fromWarsawInput, toWarsawInput } from '@/utils/warsaw-time.ts'

interface NextPresentation {
  title: string
  speakers: string
  startsAt: string
}

export type RoomState =
  | { type: 'current'; title: string; speakers: string; endsAt: string }
  | {
      type: 'break'
      label: string
      endsAt: string
      next: NextPresentation | null
    }
  | { type: 'upcoming'; next: NextPresentation }
  | { type: 'ended' }

// --- Time helpers ---

function timeToMinutes(timeStr: string): number {
  const [h, m] = timeStr.split(':').map(Number)
  return h * 60 + m
}

// The Warsaw date and minute of the day (with seconds as a fraction). Warsaw's UTC offset is a whole
// number of minutes, so the seconds are the same as UTC's.
function warsawNow() {
  const [date, time] = toWarsawInput(new Date().toISOString()).split('T')
  return { date, minutes: timeToMinutes(time) + (Date.now() % 60_000) / 60_000 }
}

function speakersLabel(p: FullPresentation): string {
  return (
    p.flatSpeakers ||
    p.speakers
      .map((s) => s.name || '')
      .filter(Boolean)
      .join(', ')
  )
}

// The room door screens' shared logic: loads today's agenda (or the next day that has one), ticks
// every second, reloads when the Warsaw date changes (and a minute after a failed load), and works out what any room shows right now.
export function useRoomDoor(loadErrorMessage = 'Failed to load schedule') {
  const loading = ref(true)
  const error = ref<string | null>(null)
  const rooms = ref<InlineRoom[]>([])
  const timeSlots = ref<InlineTimeSlot[]>([])
  const agendaEntries = ref<InlineAgendaEntry[]>([])
  const presentationsMap = ref(new Map<string, FullPresentation>())
  const tickCount = ref(0)
  const day = ref('')
  let loadedOn = ''
  // Room ids seen so far -> their labels. Every day has its own room records (ids), so a single-room
  // screen finds its room on a later day by label, the only thing a room keeps across days.
  const seenLabels = new Map<string, string>()
  // When to retry a failed load (about once a minute); Infinity while nothing is to be retried
  let retryAt = Infinity

  // Time left until `targetTimeStr` on the day being shown, in Warsaw time.
  function formatCountdown(targetTimeStr: string): string {
    const sec = Number(targetTimeStr.split(':')[2] ?? 0)
    const target =
      new Date(fromWarsawInput(`${day.value}T${targetTimeStr.slice(0, 5)}`)).getTime() + sec * 1000
    const ms = target - Date.now()
    if (ms <= 0) return '00:00'
    const totalSeconds = Math.floor(ms / 1000)
    if (totalSeconds >= 60) {
      const mins = Math.floor(totalSeconds / 60)
      return `${mins} minute${mins !== 1 ? 's' : ''}`
    }
    return `00:${String(totalSeconds).padStart(2, '0')}`
  }

  function findNextPresentation(
    roomId: string,
    fromDisplayOrder: number,
  ): NextPresentation | null {
    const sortedSlots = [...timeSlots.value].sort(
      (a, b) => a.displayOrder - b.displayOrder,
    )

    for (const slot of sortedSlots) {
      if (slot.displayOrder < fromDisplayOrder) continue

      // Skip break slots (all-rooms entries with a label and no roomId)
      const isBreakSlot = agendaEntries.value.some(
        (e) => e.timeSlotIndex === slot.displayOrder && !e.roomId && e.label,
      )
      if (isBreakSlot) continue

      const entry = agendaEntries.value.find(
        (e) =>
          e.roomId === roomId &&
          e.timeSlotIndex === slot.displayOrder &&
          e.presentationId,
      )
      if (entry?.presentationId) {
        const p = presentationsMap.value.get(entry.presentationId)
        if (p) {
          return {
            title: p.title,
            speakers: speakersLabel(p),
            startsAt: slot.start,
          }
        }
      }
    }
    return null
  }

  function roomStateFor(roomId: string): RoomState {
    // tickCount dependency ensures the timer triggers recomputation
    void tickCount.value

    const warsaw = warsawNow()
    // A day still ahead has not started: nothing on it is current, whatever the time of day
    const now = day.value > warsaw.date ? -1 : warsaw.minutes
    const sortedSlots = [...timeSlots.value].sort(
      (a, b) => a.displayOrder - b.displayOrder,
    )

    const currentSlot = sortedSlots.find(
      (ts) => timeToMinutes(ts.start) <= now && now < timeToMinutes(ts.end),
    )

    if (currentSlot) {
      // Break: an all-rooms entry with a label and no roomId
      const breakEntry = agendaEntries.value.find(
        (e) =>
          e.timeSlotIndex === currentSlot.displayOrder && !e.roomId && e.label,
      )
      if (breakEntry) {
        return {
          type: 'break',
          label: breakEntry.label!,
          endsAt: currentSlot.end,
          next: findNextPresentation(roomId, currentSlot.displayOrder + 1),
        }
      }

      // Regular slot — check if this room has a presentation scheduled
      const entry = agendaEntries.value.find(
        (e) =>
          e.roomId === roomId &&
          e.timeSlotIndex === currentSlot.displayOrder &&
          e.presentationId,
      )
      if (entry?.presentationId) {
        const p = presentationsMap.value.get(entry.presentationId)
        if (p) {
          return {
            type: 'current',
            title: p.title,
            speakers: speakersLabel(p),
            endsAt: currentSlot.end,
          }
        }
      }

      // Slot exists but nothing scheduled for this room → look ahead
      const next = findNextPresentation(roomId, currentSlot.displayOrder + 1)
      if (next) return { type: 'upcoming', next }
      return { type: 'ended' }
    }

    // Between slots — find the next one
    const nextSlot = sortedSlots.find((ts) => timeToMinutes(ts.start) > now)
    if (!nextSlot) return { type: 'ended' }

    const next = findNextPresentation(roomId, nextSlot.displayOrder)
    if (next) return { type: 'upcoming', next }
    return { type: 'ended' }
  }

  const normalize = (label?: string) => label?.trim().toLowerCase()

  // The room with this id, else the room of the picked day that has the label this id had earlier
  function findRoom(roomId: string): InlineRoom | undefined {
    const label = normalize(seenLabels.get(roomId))
    return (
      rooms.value.find((r) => r.id === roomId) ??
      (label ? rooms.value.find((r) => normalize(r.label) === label) : undefined)
    )
  }

  let timer: ReturnType<typeof setInterval> | null = null
  // Set when the screen is left: a load still in flight must not start a timer or call the API further
  let unmounted = false

  async function load() {
    const today = warsawNow().date
    loadedOn = today
    retryAt = Infinity
    try {
      const daysResponse = await getAllDays()
      if (unmounted) return
      const days = daysResponse.data

      const todayDay =
        days.find((d) => d.date === today) ??
        [...days]
          .sort((a, b) => a.date.localeCompare(b.date))
          .find((d) => d.date > today) ??
        null

      if (!todayDay) {
        error.value = 'No schedule for today'
        return
      }

      const [roomsRes, slotsRes, entriesRes, presRes] = await Promise.all([
        getAllRooms1({ path: { dayId: todayDay.id } }),
        getAllTimeSlots({ path: { dayId: todayDay.id } }),
        getAgendaEntriesByDay({ path: { dayId: todayDay.id } }),
        getAllPresentations(),
      ])

      for (const r of roomsRes.data) seenLabels.set(r.id, r.label)
      rooms.value = roomsRes.data.sort(
        (a, b) => a.displayOrder - b.displayOrder,
      )
      timeSlots.value = slotsRes.data
      agendaEntries.value = entriesRes.data
      day.value = todayDay.date

      const map = new Map<string, FullPresentation>()
      for (const p of presRes.data) {
        map.set(p.id, p)
      }
      presentationsMap.value = map
      error.value = null
    } catch {
      error.value = loadErrorMessage
      retryAt = Date.now() + 60_000
    } finally {
      loading.value = false
    }
  }

  onMounted(async () => {
    await load()
    if (unmounted) return
    timer = setInterval(() => {
      tickCount.value++
      if (warsawNow().date !== loadedOn || Date.now() >= retryAt) void load()
    }, 1_000)
  })

  onUnmounted(() => {
    unmounted = true
    if (timer) clearInterval(timer)
  })

  return { loading, error, rooms, findRoom, roomStateFor, formatCountdown }
}
