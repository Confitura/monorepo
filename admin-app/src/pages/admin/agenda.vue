<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AgendaEditor from "@/components/admin/agenda-editor.vue";
import {daysApi} from "@/utils/api.ts";
import type {InlineDay} from "@/utils/api-axios-client";

definePage({
  meta: {
    icon: 'mdi-calendar',
    title: 'Agenda Management',
    drawerIndex: 1,
  },
})

// State for conference days
const days: Ref<InlineDay[]> = ref([]);
const loading: Ref<Boolean> = ref(true);
const error: Ref<string | null> = ref(null);
const tab: Ref<string | null> = ref(null);

// Load conference days from API
onMounted(async () => {
  try {
    const response = await daysApi.getAllDays();
    days.value = response.data;

  } catch (err) {
    console.error('Error loading conference days:', err);
    error.value = 'Failed to load conference days';
    Notify.error('Failed to load conference days');
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
            {{ day.label }} - {{ day.date }}
          </v-card-title>
          <v-card-text v-if="day.id">
            <agenda-editor :day-id="day.id"></agenda-editor>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>
  </div>
</template>

<style scoped lang="scss">
</style>
