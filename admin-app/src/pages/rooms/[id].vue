<script setup lang="ts">
import { useRoomDoor } from '@/composables/useRoomDoor.ts'

definePage({
  meta: {
    title: 'Room',
    layout: 'no-distractions',
  },
})

const route = useRoute('/rooms/[id]')
const roomId = route.params.id as string

const { loading, error: loadError, rooms, roomStateFor, formatCountdown } =
  useRoomDoor()

const room = computed(() => rooms.value.find((r) => r.id === roomId))
const error = computed(
  () => loadError.value ?? (room.value ? null : 'Room not found'),
)
const roomState = computed(() => roomStateFor(roomId))
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
      <div class="room-name text-overline mb-2">{{ room?.label }}</div>

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
