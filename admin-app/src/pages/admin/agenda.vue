<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import AgendaEditor from "@/components/admin/agenda-editor.vue";
import {daysApi} from "@/utils/api.ts";

definePage({
  meta: {
    icon: 'mdi-calendar',
    title: 'Agenda Management',
    drawerIndex: 1,
  },
})

// State for conference days
const days = ref([]);
const loading = ref(true);
const error = ref(null);
const tab = ref(null);

// Load conference days from API
onMounted(async () => {
  try {
    const response = await daysApi.getAllDays();
    days.value = response.data;

    // If no days exist yet, create default days
    if (days.value.length === 0) {
      days.value = [
        { id: 'day1', label: 'Day 1', date: '2025-09-01', displayOrder: 1 },
        { id: 'day2', label: 'Day 2', date: '2025-09-02', displayOrder: 2 }
      ];
    }
  } catch (err) {
    console.error('Error loading conference days:', err);
    error.value = 'Failed to load conference days. Using default days instead.';

    // Fallback to default days
    days.value = [
      { id: 'day1', label: 'Day 1', date: '2025-09-01', displayOrder: 1 },
      { id: 'day2', label: 'Day 2', date: '2025-09-02', displayOrder: 2 }
    ];
  } finally {
    loading.value = false;

    // Set the initial tab to the first day
    if (days.value.length > 0) {
      tab.value = days.value[0].id;
    }
  }
});
</script>

<template>
  <div>
    <v-alert v-if="error" type="warning" class="mb-4">
      {{ error }}
    </v-alert>

    <v-tabs v-model="tab">
      <v-tab v-for="day in days" :key="day.id" :value="day.id">
        {{ day.label }}
      </v-tab>
    </v-tabs>

    <v-window v-model="tab">
      <v-window-item v-for="day in days" :key="day.id" :value="day.id">
        <v-card class="mt-4">
          <v-card-title>
            {{ day.label }} - {{ new Date(day.date).toLocaleDateString() }}
          </v-card-title>
          <v-card-text>
            <agenda-editor :day-id="day.id"></agenda-editor>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>
  </div>
</template>

<style scoped lang="scss">
</style>
