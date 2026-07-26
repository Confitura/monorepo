<script setup lang="ts">
import { agendaApi, daysApi } from '@/utils/api.ts'
import type { InlineRoom } from '@/utils/api-axios-client'

definePage({
  meta: {
    title: 'Rooms',
    layout: 'no-distractions',
  },
})

const loading = ref(true)
const error = ref<string | null>(null)
const rooms = ref<InlineRoom[]>([])

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

    const roomsResponse = await agendaApi.getAllRooms1(todayDay.id)
    rooms.value = roomsResponse.data.sort((a, b) => a.displayOrder - b.displayOrder)
  } catch {
    error.value = 'Failed to load rooms'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <v-container class="py-8" max-width="600">
    <v-row justify="center">
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold mb-6 text-center">Rooms</h1>

        <div v-if="loading" class="d-flex justify-center">
          <v-progress-circular indeterminate color="primary" />
        </div>

        <v-alert v-else-if="error" type="info" variant="tonal" class="text-center">
          {{ error }}
        </v-alert>

        <v-list v-else lines="one">
          <v-list-item
            v-for="room in rooms"
            :key="room.id"
            :to="`/rooms/${room.id}`"
            :title="room.label"
            append-icon="mdi-chevron-right"
            rounded="lg"
            class="mb-2 border"
          />
        </v-list>
      </v-col>
    </v-row>
  </v-container>
</template>
