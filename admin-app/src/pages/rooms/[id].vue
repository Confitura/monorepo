<script setup lang="ts">
import { agendaApi, daysApi, presentationApi } from '@/utils/api.ts'
import type { InlineAgendaEntry, InlineTimeSlot, FullPresentation } from '@/utils/api-axios-client'

definePage({
  meta: {
    title: 'Room',
    layout: 'no-distractions',
  },
})

const route = useRoute()
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
  const mins = Math.floor(totalSeconds / 60)
  const secs = totalSeconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
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

// --- State computation ---

function speakersLabel(p: FullPresentation): string {
  return p.flatSpeakers || p.speakers.map(s => s.name || '').filter(Boolean).join(', ')
}

function findNextPresentation(fromDisplayOrder: number): NextPresentation | null {
  const sortedSlots = [...timeSlots.value].sort((a, b) => a.displayOrder - b.displayOrder)

  for (const slot of sortedSlots) {
    if (slot.displayOrder < fromDisplayOrder) continue

    // Skip break slots (all-rooms entries with a label and no roomId)
    const isBreakSlot = agendaEntries.value.some(
      e => e.timeSlotIndex === slot.displayOrder && !e.roomId && e.label,
    )
    if (isBreakSlot) continue

    const entry = agendaEntries.value.find(
      e => e.roomId === roomId && e.timeSlotIndex === slot.displayOrder && e.presentationId,
    )
    if (entry?.presentationId) {
      const p = presentationsMap.value.get(entry.presentationId)
      if (p) {
        return { title: p.title, speakers: speakersLabel(p), startsAt: slot.start }
      }
    }
  }
  return null
}

const roomState = computed((): RoomState => {
  // tickCount dependency ensures the timer triggers recomputation
  void tickCount.value

  const now = nowMinutes()
  const sortedSlots = [...timeSlots.value].sort((a, b) => a.displayOrder - b.displayOrder)

  const currentSlot = sortedSlots.find(
    ts => timeToMinutes(ts.start) <= now && now < timeToMinutes(ts.end),
  )

  if (currentSlot) {
    // Break: an all-rooms entry with a label and no roomId
    const breakEntry = agendaEntries.value.find(
      e => e.timeSlotIndex === currentSlot.displayOrder && !e.roomId && e.label,
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
      e => e.roomId === roomId && e.timeSlotIndex === currentSlot.displayOrder && e.presentationId,
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
  const nextSlot = sortedSlots.find(ts => timeToMinutes(ts.start) > now)
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
  const target = targetTime.value
  const ms = target ? remainingMs(target) : null
  const interval = ms !== null && ms < 60_000 ? 1_000 : 30_000
  timer = setInterval(tick, interval)
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

    const room = roomsRes.data.find(r => r.id === roomId)
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
  <v-container class="py-8" max-width="600">
    <v-row justify="center">
      <v-col cols="12">
        <v-btn variant="text" to="/rooms" prepend-icon="mdi-arrow-left" class="mb-4 pl-0">
          All Rooms
        </v-btn>

        <div v-if="loading" class="d-flex justify-center">
          <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else-if="error">
          <v-alert type="info" variant="tonal" class="text-center">{{ error }}</v-alert>
        </template>

        <template v-else>
          <h1 class="text-h4 font-weight-bold mb-6 text-center">{{ roomLabel }}</h1>

          <!-- Presentation currently in progress -->
          <v-card
            v-if="roomState.type === 'current'"
            color="primary"
            variant="flat"
            rounded="xl"
            class="text-center pa-6"
          >
            <div class="text-overline mb-2 opacity-70">Now</div>
            <div class="text-h5 font-weight-bold mb-2">{{ roomState.title }}</div>
            <div class="text-subtitle-1 mb-8 opacity-90">{{ roomState.speakers }}</div>
            <div class="text-overline opacity-70">Ends in</div>
            <div class="text-h2 font-weight-bold" style="font-variant-numeric: tabular-nums">
              {{ formatCountdown(roomState.endsAt) }}
            </div>
          </v-card>

          <!-- Break / all-rooms slot -->
          <v-card
            v-else-if="roomState.type === 'break'"
            color="secondary"
            variant="flat"
            rounded="xl"
            class="text-center pa-6"
          >
            <div class="text-overline mb-2 opacity-70">Break</div>
            <div class="text-h5 font-weight-bold mb-6">{{ roomState.label }}</div>
            <div class="text-overline opacity-70">Ends in</div>
            <div class="text-h2 font-weight-bold mb-6" style="font-variant-numeric: tabular-nums">
              {{ formatCountdown(roomState.endsAt) }}
            </div>
            <template v-if="roomState.next">
              <v-divider class="mb-4 opacity-30" />
              <div class="text-overline opacity-70">Up next</div>
              <div class="text-h6 font-weight-medium mt-1">{{ roomState.next.title }}</div>
              <div class="text-body-2 mt-1 opacity-80">{{ roomState.next.speakers }}</div>
            </template>
          </v-card>

          <!-- Upcoming presentation -->
          <v-card
            v-else-if="roomState.type === 'upcoming'"
            color="surface-variant"
            variant="flat"
            rounded="xl"
            class="text-center pa-6"
          >
            <div class="text-overline mb-2 opacity-70">Next</div>
            <div class="text-h5 font-weight-bold mb-2">{{ roomState.next.title }}</div>
            <div class="text-subtitle-1 mb-8 opacity-90">{{ roomState.next.speakers }}</div>
            <div class="text-overline opacity-70">Starts in</div>
            <div class="text-h2 font-weight-bold" style="font-variant-numeric: tabular-nums">
              {{ formatCountdown(roomState.next.startsAt) }}
            </div>
          </v-card>

          <!-- No more presentations today -->
          <v-card
            v-else
            color="surface-variant"
            variant="tonal"
            rounded="xl"
            class="text-center pa-8"
          >
            <v-icon size="48" class="mb-4 opacity-50">mdi-calendar-check</v-icon>
            <div class="text-h6">No more presentations today</div>
          </v-card>
        </template>
      </v-col>
    </v-row>
  </v-container>
</template>
