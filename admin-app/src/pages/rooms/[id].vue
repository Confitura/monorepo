<script setup lang="ts">
import {
  getAgendaEntriesByDay,
  getAllDays,
  getAllPresentations,
  getAllRooms1,
  getAllTimeSlots,
  type FullPresentation,
  type InlineAgendaEntry,
  type InlineTimeSlot,
} from '@/utils/api.ts'

definePage({
  meta: {
    title: 'Room',
    layout: 'no-distractions',
  },
})

const route = useRoute('/rooms/[id]')
const roomId = route.params.id as string

const loading = ref(true)
const error = ref<string | null>(null)
const roomLabel = ref('')
const timeSlots = ref<InlineTimeSlot[]>([])
const agendaEntries = ref<InlineAgendaEntry[]>([])
const presentationsMap = ref(new Map<string, FullPresentation>())
const tickCount = ref(0)

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

function formatCountdown(targetTimeStr: string): string {
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

// --- State types ---

interface NextPresentation {
  title: string
  speakers: string
  startsAt: string
}

type RoomState =
  | { type: 'current'; title: string; speakers: string; endsAt: string }
  | {
      type: 'break'
      label: string
      endsAt: string
      next: NextPresentation | null
    }
  | { type: 'upcoming'; next: NextPresentation }
  | { type: 'ended' }

// --- State computation ---

function speakersLabel(p: FullPresentation): string {
  return (
    p.flatSpeakers ||
    p.speakers
      .map((s) => s.name || '')
      .filter(Boolean)
      .join(', ')
  )
}

function findNextPresentation(
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

const roomState = computed((): RoomState => {
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
        next: findNextPresentation(currentSlot.displayOrder + 1),
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
    const next = findNextPresentation(currentSlot.displayOrder + 1)
    if (next) return { type: 'upcoming', next }
    return { type: 'ended' }
  }

  // Between slots — find the next one
  const nextSlot = sortedSlots.find((ts) => timeToMinutes(ts.start) > now)
  if (!nextSlot) return { type: 'ended' }

  const next = findNextPresentation(nextSlot.displayOrder)
  if (next) return { type: 'upcoming', next }
  return { type: 'ended' }
})

const targetTime = computed((): string | null => {
  const s = roomState.value
  if (s.type === 'current') return s.endsAt
  if (s.type === 'break') return s.endsAt
  if (s.type === 'upcoming') return s.next.startsAt
  return null
})

// --- Adaptive timer ---

let timer: ReturnType<typeof setInterval> | null = null

function reschedule() {
  if (timer) clearInterval(timer)
  timer = setInterval(tick, 1_000)
}

function tick() {
  tickCount.value++
  reschedule()
}

// --- Data loading ---

onMounted(async () => {
  try {
    const today = new Date().toISOString().split('T')[0]
    const daysResponse = await getAllDays({ throwOnError: true })
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
      getAllRooms1({ path: { dayId: todayDay.id }, throwOnError: true }),
      getAllTimeSlots({ path: { dayId: todayDay.id }, throwOnError: true }),
      getAgendaEntriesByDay({ path: { dayId: todayDay.id }, throwOnError: true }),
      getAllPresentations({ throwOnError: true }),
    ])

    const room = roomsRes.data.find((r) => r.id === roomId)
    if (!room) {
      error.value = 'Room not found'
      return
    }

    roomLabel.value = room.label
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
    reschedule()
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <div class="room-page">
    <!-- Back button always visible at top-left -->
    <v-btn
      variant="text"
      to="/rooms"
      prepend-icon="mdi-arrow-left"
      class="back-btn"
      size="small"
    >
      Rooms
    </v-btn>

    <!-- Loading -->
    <div v-if="loading" class="fill-center">
      <v-progress-circular indeterminate color="primary" size="64" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="fill-center">
      <v-alert
        type="info"
        variant="tonal"
        class="text-center"
        max-width="400"
        >{{ error }}</v-alert
      >
    </div>

    <!-- Content -->
    <div v-else class="fill-center flex-column">
      <div class="room-name text-overline mb-2">{{ roomLabel }}</div>

      <!-- Presentation currently in progress -->
      <template v-if="roomState.type === 'current'">
        <div class="state-label">NOW</div>
        <div class="presentation-title">{{ roomState.title }}</div>
        <div class="speakers-label">{{ roomState.speakers }}</div>
        <div class="countdown-label">ends in</div>
        <div class="countdown">{{ formatCountdown(roomState.endsAt) }}</div>
      </template>

      <!-- Break / all-rooms slot -->
      <template v-else-if="roomState.type === 'break'">
        <div class="state-label break-state">BREAK</div>
        <div class="presentation-title">{{ roomState.label }}</div>
        <div class="countdown-label">ends in</div>
        <div class="countdown break-countdown">
          {{ formatCountdown(roomState.endsAt) }}
        </div>
        <template v-if="roomState.next">
          <div class="next-divider" />
          <div class="next-label">UP NEXT</div>
          <div class="next-title">{{ roomState.next.title }}</div>
          <div class="next-speakers">{{ roomState.next.speakers }}</div>
        </template>
      </template>

      <!-- Upcoming presentation -->
      <template v-else-if="roomState.type === 'upcoming'">
        <div class="state-label upcoming-state">NEXT</div>
        <div class="presentation-title">{{ roomState.next.title }}</div>
        <div class="speakers-label">{{ roomState.next.speakers }}</div>
        <div class="countdown-label">starts in</div>
        <div class="countdown upcoming-countdown">
          {{ formatCountdown(roomState.next.startsAt) }}
        </div>
      </template>

      <!-- No more presentations today -->
      <template v-else>
        <v-icon size="80" class="mb-6 opacity-40">mdi-calendar-check</v-icon>
        <div class="presentation-title opacity-60">
          No more presentations today
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.room-page {
  position: relative;
  width: 100%;
  min-height: 100%;
  display: flex;
  align-items: stretch;
}

.back-btn {
  position: absolute;
  top: 12px;
  left: 8px;
  z-index: 10;
}

.fill-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 80px 24px 40px;
}

.flex-column {
  flex-direction: column;
}

.room-name {
  font-size: 0.85rem;
  letter-spacing: 0.15em;
  opacity: 0.5;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.state-label {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: rgb(var(--v-theme-primary));
  margin-bottom: 16px;
}

.break-state {
  color: rgb(var(--v-theme-secondary));
}

.upcoming-state {
  opacity: 0.7;
}

.presentation-title {
  font-size: clamp(1.6rem, 5vw, 3rem);
  font-weight: 800;
  line-height: 1.15;
  max-width: 800px;
  margin-bottom: 12px;
}

.speakers-label {
  font-size: clamp(1rem, 2.5vw, 1.4rem);
  opacity: 0.75;
  margin-bottom: 40px;
  max-width: 700px;
}

.countdown-label {
  font-size: 0.85rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  opacity: 0.5;
  margin-bottom: 8px;
}

.countdown {
  font-size: clamp(4rem, 18vw, 12rem);
  font-weight: 900;
  line-height: 1;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.02em;
  color: rgb(var(--v-theme-primary));
}

.break-countdown {
  color: rgb(var(--v-theme-secondary));
}

.upcoming-countdown {
  opacity: 0.8;
}

.next-divider {
  width: 40px;
  height: 2px;
  background: currentColor;
  opacity: 0.2;
  margin: 32px auto 24px;
}

.next-label {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  opacity: 0.5;
  margin-bottom: 8px;
}

.next-title {
  font-size: clamp(1rem, 2.5vw, 1.5rem);
  font-weight: 600;
  max-width: 700px;
  margin-bottom: 4px;
}

.next-speakers {
  font-size: 1rem;
  opacity: 0.65;
}
</style>
