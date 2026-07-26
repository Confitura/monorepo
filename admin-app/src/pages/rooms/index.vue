<script setup lang="ts">
import { agendaApi, daysApi, presentationApi } from '@/utils/api.ts'
import type { InlineAgendaEntry, InlineRoom, InlineTimeSlot, FullPresentation } from '@/utils/api-axios-client'

definePage({
  meta: {
    title: 'Rooms',
    layout: 'no-distractions',
  },
})

// --- Reactive state ---

const loading = ref(true)
const error = ref<string | null>(null)
const rooms = ref<InlineRoom[]>([])
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
  | { type: 'break'; label: string; endsAt: string; next: NextPresentation | null }
  | { type: 'upcoming'; next: NextPresentation }
  | { type: 'ended' }
  | { type: 'loading' }

// --- State computation ---

function speakersLabel(p: FullPresentation): string {
  return p.flatSpeakers || p.speakers.map(s => s.name || '').filter(Boolean).join(', ')
}

function findNextPresentation(roomId: string, fromDisplayOrder: number): NextPresentation | null {
  const sortedSlots = [...timeSlots.value].sort((a, b) => a.displayOrder - b.displayOrder)
  for (const slot of sortedSlots) {
    if (slot.displayOrder < fromDisplayOrder) continue
    const isBreakSlot = agendaEntries.value.some(
      e => e.timeSlotIndex === slot.displayOrder && !e.roomId && e.label,
    )
    if (isBreakSlot) continue
    const entry = agendaEntries.value.find(
      e => e.roomId === roomId && e.timeSlotIndex === slot.displayOrder && e.presentationId,
    )
    if (entry?.presentationId) {
      const p = presentationsMap.value.get(entry.presentationId)
      if (p) return { title: p.title, speakers: speakersLabel(p), startsAt: slot.start }
    }
  }
  return null
}

function computeRoomState(roomId: string): RoomState {
  void tickCount.value // reactive dependency
  if (loading.value) return { type: 'loading' }

  const now = nowMinutes()
  const sortedSlots = [...timeSlots.value].sort((a, b) => a.displayOrder - b.displayOrder)

  const currentSlot = sortedSlots.find(
    ts => timeToMinutes(ts.start) <= now && now < timeToMinutes(ts.end),
  )

  if (currentSlot) {
    const breakEntry = agendaEntries.value.find(
      e => e.timeSlotIndex === currentSlot.displayOrder && !e.roomId && e.label,
    )
    if (breakEntry) {
      return {
        type: 'break',
        label: breakEntry.label!,
        endsAt: currentSlot.end,
        next: findNextPresentation(roomId, currentSlot.displayOrder + 1),
      }
    }
    const entry = agendaEntries.value.find(
      e => e.roomId === roomId && e.timeSlotIndex === currentSlot.displayOrder && e.presentationId,
    )
    if (entry?.presentationId) {
      const p = presentationsMap.value.get(entry.presentationId)
      if (p) return { type: 'current', title: p.title, speakers: speakersLabel(p), endsAt: currentSlot.end }
    }
    const next = findNextPresentation(roomId, currentSlot.displayOrder + 1)
    if (next) return { type: 'upcoming', next }
    return { type: 'ended' }
  }

  const nextSlot = sortedSlots.find(ts => timeToMinutes(ts.start) > now)
  if (!nextSlot) return { type: 'ended' }
  const next = findNextPresentation(roomId, nextSlot.displayOrder)
  if (next) return { type: 'upcoming', next }
  return { type: 'ended' }
}

function cardColor(_state: RoomState): string {
  return '#000000'
}

function stateLabel(state: RoomState): string {
  if (state.type === 'current') return 'NOW'
  if (state.type === 'break') return 'BREAK'
  if (state.type === 'upcoming') return 'NEXT'
  return ''
}

function stateCountdown(state: RoomState): string {
  if (state.type === 'current') return `ends in ${formatCountdown(state.endsAt)}`
  if (state.type === 'break') return `ends in ${formatCountdown(state.endsAt)}`
  if (state.type === 'upcoming') return `starts in ${formatCountdown(state.next.startsAt)}`
  return ''
}

// --- Derived list (room + computed state, reactive to tickCount) ---

const roomsWithState = computed(() => {
  void tickCount.value
  return rooms.value.map(r => ({ room: r, state: computeRoomState(r.id) }))
})

// --- Adaptive timer ---

let timer: ReturnType<typeof setInterval> | null = null

function reschedule() {
  if (timer) clearInterval(timer)
  if (rooms.value.length === 0) {
    timer = setInterval(tick, 30_000)
    return
  }
  const minMs = rooms.value
    .map(r => {
      const s = computeRoomState(r.id)
      const t =
        s.type === 'current' ? s.endsAt
        : s.type === 'break' ? s.endsAt
        : s.type === 'upcoming' ? s.next.startsAt
        : null
      return t ? remainingMs(t) : Infinity
    })
    .reduce((a, b) => Math.min(a, b), Infinity)
  timer = setInterval(tick, minMs < 60_000 ? 1_000 : 30_000)
}

function tick() {
  tickCount.value++
  reschedule()
}

// --- Data loading ---

onMounted(async () => {
  try {
    const today = new Date().toISOString().split('T')[0]
    const daysResponse = await daysApi.getAllDays()
    const days = daysResponse.data
    const todayDay =
      days.find(d => d.date === today) ??
      [...days].sort((a, b) => a.date.localeCompare(b.date)).find(d => d.date > today) ??
      null

    if (!todayDay) {
      error.value = 'No schedule for today'
      return
    }

    const [roomsRes, slotsRes, entriesRes, presRes] = await Promise.all([
      agendaApi.getAllRooms1(todayDay.id),
      agendaApi.getAllTimeSlots(todayDay.id),
      agendaApi.getAgendaEntriesByDay(todayDay.id),
      presentationApi.getAllPresentations(),
    ])

    rooms.value = roomsRes.data.sort((a, b) => a.displayOrder - b.displayOrder)
    timeSlots.value = slotsRes.data
    agendaEntries.value = entriesRes.data

    const map = new Map<string, FullPresentation>()
    for (const p of presRes.data) map.set(p.id, p)
    presentationsMap.value = map
  } catch {
    error.value = 'Failed to load rooms'
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
  <div class="rooms-page">
    <div v-if="loading" class="rooms-center">
      <v-progress-circular indeterminate color="primary" size="64" />
    </div>

    <v-alert v-else-if="error" type="info" variant="tonal" class="ma-6 text-center">
      {{ error }}
    </v-alert>

    <div v-else class="rooms-grid">
      <v-card
        v-for="{ room, state } in roomsWithState"
        :key="room.id"
        :to="`/rooms/${room.id}`"
        :color="cardColor(state)"
        variant="flat"
        rounded="0"
        class="room-card"
      >
        <v-card-text class="room-card-content">
          <!-- Room name + state badge row -->
          <div class="d-flex justify-space-between align-center">
            <span class="room-label text-primary font-weight-bold">{{ room.label }}</span>
            <span
              v-if="stateLabel(state)"
              class="state-badge text-primary font-weight-bold"
              style="font-variant-numeric:tabular-nums"
            >
              {{ stateLabel(state) }} · {{ stateCountdown(state) }}
            </span>
          </div>

          <!-- Title -->
          <div class="presentation-title">
            <template v-if="state.type === 'current'">{{ state.title }}</template>
            <template v-else-if="state.type === 'break'">{{ state.label }}</template>
            <template v-else-if="state.type === 'upcoming'">{{ state.next.title }}</template>
            <template v-else><span class="text-medium-emphasis">No more presentations today</span></template>
          </div>

          <!-- Subtitle -->
          <div class="presentation-subtitle text-medium-emphasis">
            <template v-if="state.type === 'current'">{{ state.speakers }}</template>
            <template v-else-if="state.type === 'break' && state.next">Next: {{ state.next.title }}</template>
            <template v-else-if="state.type === 'upcoming'">{{ state.next.speakers }}</template>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<style scoped>
.rooms-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.rooms-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rooms-grid {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  gap: 2px;
  max-width: 680px;
  width: 100%;
  margin: 0 auto;
}

.room-card {
  flex: 1;
  min-height: 0;
}

/* Make v-card-text fill the full card height */
.room-card :deep(.v-card-text) {
  height: 100%;
}

.room-card-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px 24px;
  gap: 4px;
}

.room-label {
  font-size: clamp(0.75rem, 1.5vh, 1rem);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.state-badge {
  font-size: clamp(0.65rem, 1.2vh, 0.875rem);
  letter-spacing: 0.08em;
}

.presentation-title {
  font-size: clamp(1.1rem, 3vh, 2rem);
  font-weight: 700;
  line-height: 1.2;
}

.presentation-subtitle {
  font-size: clamp(0.8rem, 1.8vh, 1.1rem);
  line-height: 1.3;
  margin-top: 2px;
}
</style>
