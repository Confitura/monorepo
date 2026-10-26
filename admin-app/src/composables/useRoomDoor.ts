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

function nowMinutes(): number {
  const now = new Date()
  return now.getHours() * 60 + now.getMinutes() + now.getSeconds() / 60
}

function timeStrToDate(timeStr: string): Date {
  const [h, m, s = 0] = timeStr.split(':').map(Number)
  const d = new Date()
  d.setHours(h, m, s, 0)
  return d
}

function remainingMs(targetTimeStr: string): number {
  return timeStrToDate(targetTimeStr).getTime() - Date.now()
}

export function formatCountdown(targetTimeStr: string): string {
  const ms = remainingMs(targetTimeStr)
  if (ms <= 0) return '00:00'
  const totalSeconds = Math.floor(ms / 1000)
  if (totalSeconds > 60) {
    const mins = Math.floor(totalSeconds / 60)
    return `${mins} minute${mins !== 1 ? 's' : ''}`
  }
  const secs = totalSeconds % 60
  return `00:${String(secs).padStart(2, '0')}`
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
// every second, and works out what any room shows right now.
export function useRoomDoor() {
  const loading = ref(true)
  const error = ref<string | null>(null)
  const rooms = ref<InlineRoom[]>([])
  const timeSlots = ref<InlineTimeSlot[]>([])
  const agendaEntries = ref<InlineAgendaEntry[]>([])
  const presentationsMap = ref(new Map<string, FullPresentation>())
  const tickCount = ref(0)

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

    const now = nowMinutes()
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

  let timer: ReturnType<typeof setInterval> | null = null

  onMounted(async () => {
    try {
      const today = new Date().toISOString().split('T')[0]
      const daysResponse = await getAllDays()
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

      rooms.value = roomsRes.data.sort(
        (a, b) => a.displayOrder - b.displayOrder,
      )
      timeSlots.value = slotsRes.data
      agendaEntries.value = entriesRes.data

      const map = new Map<string, FullPresentation>()
      for (const p of presRes.data) {
        map.set(p.id, p)
      }
      presentationsMap.value = map
    } catch {
      error.value = 'Failed to load schedule'
    } finally {
      loading.value = false
      timer = setInterval(() => tickCount.value++, 1_000)
    }
  })

  onUnmounted(() => {
    if (timer) clearInterval(timer)
  })

  return { loading, error, rooms, roomStateFor, formatCountdown }
}
