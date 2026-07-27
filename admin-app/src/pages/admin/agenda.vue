<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AgendaEditor from "@/components/admin/agenda-editor.vue";
import DialogConfirm from '@/components/DialogConfirm.vue';
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

const confirmDialog = useTemplateRef('confirmDialog');
const dayDialog = ref(false);
const dayFormValid = ref(false);
const editedDay = ref({id: '', label: '', date: '', displayOrder: 1});
const editMode = ref(false);

const requiredRule = (value: string) => !!value || 'This field is required';

// Load conference days from API
async function loadDays() {
  try {
    const response = await daysApi.getAllDays();
    days.value = response.data;
    error.value = null;
  } catch (err) {
    console.error('Error loading conference days:', err);
    error.value = 'Failed to load conference days';
    Notify.error('Failed to load conference days');
  } finally {
    loading.value = false;

    // Set the initial tab to the first day
    if (days.value.length > 0 && !days.value.some(day => day.id === tab.value)) {
      tab.value = days.value[0].id;
    }
  }
}

onMounted(loadDays);

function addDay() {
  editedDay.value = {
    id: '',
    label: '',
    date: '',
    displayOrder: days.value.length + 1,
  };
  editMode.value = false;
  dayDialog.value = true;
}

function editDay(day: InlineDay) {
  editedDay.value = {
    id: day.id,
    label: day.label,
    date: day.date,
    displayOrder: day.displayOrder,
  };
  editMode.value = true;
  dayDialog.value = true;
}

async function saveDay() {
  const form = editedDay.value;
  try {
    if (editMode.value) {
      await daysApi.updateDay(form.id, {
        label: form.label,
        date: form.date,
        displayOrder: form.displayOrder,
      });
    } else {
      await daysApi.saveDay(form);
    }
    dayDialog.value = false;
    await loadDays();
    tab.value = form.id;
  } catch (err) {
    console.error('Error saving conference day:', err);
    Notify.error('Failed to save conference day');
  }
}

function deleteDay(day: InlineDay) {
  confirmDialog.value
      ?.open(`Are you sure you want to DELETE ${day.label} with all its rooms, time slots and agenda entries?`)
      .then(async (confirmed: boolean) => {
        if (!confirmed) return;
        try {
          await daysApi.deleteDay(day.id);
          tab.value = null;
          await loadDays();
        } catch (err) {
          console.error('Error deleting conference day:', err);
          Notify.error('Failed to delete conference day');
        }
      });
}
</script>

<template>
  <div>
    <v-alert v-if="error" type="warning" class="mb-4">
      {{ error }}
    </v-alert>

    <div class="d-flex align-center">
      <v-tabs v-model="tab">
        <v-tab v-for="day in days" :key="day.id" :value="day.id">
          {{ day.label }}
        </v-tab>
      </v-tabs>
      <v-spacer/>
      <v-btn color="primary" variant="tonal" prepend-icon="mdi-plus" @click="addDay">
        Add Day
      </v-btn>
    </div>

    <v-alert v-if="!loading && days.length === 0" type="info" variant="tonal" class="mt-4">
      There are no conference days yet. Add the first one to start building the agenda.
    </v-alert>

    <v-window v-model="tab">
      <v-window-item v-for="day in days" :key="day.id" :value="day.id">
        <v-card class="mt-4">
          <v-card-title class="d-flex align-center">
            <span>{{ day.label }} - {{ day.date }}</span>
            <v-spacer/>
            <v-btn icon="mdi-pencil" size="small" variant="text"
                   @click="editDay(day)"></v-btn>
            <v-btn icon="mdi-delete" size="small" variant="text" color="error"
                   @click="deleteDay(day)"></v-btn>
          </v-card-title>
          <v-card-text v-if="day.id">
            <agenda-editor :day-id="day.id"></agenda-editor>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>

    <!-- Day Dialog -->
    <v-dialog v-model="dayDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ editMode ? 'Edit Day' : 'New Day' }}</span>
        </v-card-title>

        <v-card-text>
          <v-form v-model="dayFormValid">
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="editedDay.id"
                    label="Identifier (used in the schedule URL, e.g. day-1)"
                    :disabled="editMode"
                    :rules="[requiredRule]"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                    v-model="editedDay.label"
                    label="Label"
                    :rules="[requiredRule]"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="editedDay.date"
                    label="Date"
                    type="date"
                    :rules="[requiredRule]"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model.number="editedDay.displayOrder"
                    label="Display Order"
                    type="number"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text" @click="dayDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" :disabled="!dayFormValid" @click="saveDay">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <DialogConfirm ref="confirmDialog"/>
  </div>
</template>

<style scoped lang="scss">
</style>
