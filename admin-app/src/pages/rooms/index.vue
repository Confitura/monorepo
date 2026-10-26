<script setup lang="ts">
import { useRoomDoor, type RoomState } from '@/composables/useRoomDoor.ts'

definePage({
  meta: {
    title: 'Rooms',
    layout: 'no-distractions',
  },
})

const { loading, error, rooms, roomStateFor, formatCountdown } = useRoomDoor()

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

// --- Derived list (room + state; roomStateFor re-evaluates on the composable's tick) ---

const roomsWithState = computed(() =>
  rooms.value.map(r => ({ room: r, state: roomStateFor(r.id) })),
)
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
