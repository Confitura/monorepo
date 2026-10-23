<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { getAllEditions, createEdition, updateEdition, deleteEdition } from '@/utils/api.ts'
import { fromWarsawInput, toWarsawInput } from '@/utils/warsaw-time'
import type { EditionDto } from '@/client'

definePage({
  meta: {
    title: 'Editions',
    icon: 'mdi-calendar-star',
  },
})

const editions = ref<EditionDto[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

// --- Create/edit dialog ---
// The date-times are as typed in datetime-local fields: Warsaw wall-clock time, 'YYYY-MM-DDTHH:mm'.
// The last day is a calendar date ('YYYY-MM-DD') and never goes through a time zone.
const emptyForm = () => ({
  id: '',
  year: undefined as number | undefined,
  name: '',
  conferenceStart: '',
  lastDay: '',
  c4pStart: '',
  c4pEnd: '',
})
const dialog = ref(false)
const editing = ref(false)
const form = ref(emptyForm())
// A save in flight: blocks a second Save and closing the dialog until it completes.
const saving = ref(false)
const requiredRule = (value: unknown) => !!String(value ?? '').trim() || 'This field is required'

// Mirrors the backend's refusals; the last day is compared with the conference start's Warsaw date.
const problem = computed(() => {
  const f = form.value
  if (!f.name.trim() || !f.conferenceStart || !f.lastDay || (!editing.value && !f.year)) return 'Fill in all required fields'
  if (!f.c4pStart !== !f.c4pEnd) return 'Enter both C4P dates, or neither'
  if (f.c4pStart && f.c4pEnd <= f.c4pStart) return 'C4P must end after it starts'
  if (f.lastDay < f.conferenceStart.slice(0, 10)) return 'The last day cannot be before the conference starts'
  return ''
})

const headers: DataTableHeaders = [
  { title: 'Name', key: 'name', sortable: false },
  { title: 'Conference', key: 'conference', sortable: false },
  { title: 'C4P', key: 'c4p', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false },
]

const dateTimeFormat = new Intl.DateTimeFormat('en-GB', {
  timeZone: 'Europe/Warsaw',
  dateStyle: 'medium',
  timeStyle: 'short',
})
// The last day is a calendar date with no zone: read it as UTC midnight so no zone shifts it.
const dateFormat = new Intl.DateTimeFormat('en-GB', { timeZone: 'UTC', dateStyle: 'medium' })

const formatDateTime = (instant: string) => dateTimeFormat.format(new Date(instant))
const formatDate = (date: string) => dateFormat.format(new Date(date))

// Same rule as the backend: open strictly between the C4P start and end; with no period it is closed.
function c4pOpen(item: EditionDto) {
  if (!item.c4pStart || !item.c4pEnd) return false
  const now = Date.now()
  return now > new Date(item.c4pStart).getTime() && now < new Date(item.c4pEnd).getTime()
}

function reload() {
  // throwOnError: the generated client otherwise resolves with the error, which would blank the list.
  getAllEditions({ throwOnError: true })
    .then((res) => {
      editions.value = res.data ?? []
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to load Editions')
    })
}

function openCreate() {
  editing.value = false
  form.value = emptyForm()
  dialog.value = true
}

function openEdit(item: EditionDto) {
  editing.value = true
  form.value = {
    id: item.id!,
    year: item.year,
    name: item.name ?? '',
    conferenceStart: toWarsawInput(item.conferenceStart!),
    lastDay: item.lastDay!,
    c4pStart: item.c4pStart ? toWarsawInput(item.c4pStart) : '',
    c4pEnd: item.c4pEnd ? toWarsawInput(item.c4pEnd) : '',
  }
  dialog.value = true
}

function saveRefusal(e: any) {
  switch (e?.response?.status) {
    case 409:
      return 'An Edition for this year already exists'
    case 400:
      return 'The server refused the Edition: check the dates'
    default:
      return 'Failed to save Edition'
  }
}

function save() {
  if (problem.value || saving.value) return
  const f = form.value
  const body = {
    // An update must repeat the stored year.
    year: f.year,
    name: f.name.trim(),
    conferenceStart: fromWarsawInput(f.conferenceStart),
    lastDay: f.lastDay,
    // The update replaces the whole Edition: leaving both out closes the C4P.
    c4pStart: f.c4pStart ? fromWarsawInput(f.c4pStart) : undefined,
    c4pEnd: f.c4pEnd ? fromWarsawInput(f.c4pEnd) : undefined,
  }
  saving.value = true
  // throwOnError: the generated client otherwise resolves with the error, which would look like a success.
  const request = editing.value
    ? updateEdition({ path: { id: f.id }, body, throwOnError: true })
    : createEdition({ body, throwOnError: true })
  request
    .then(() => {
      Notify.success(editing.value ? 'Edition updated' : 'Edition created')
      dialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error(saveRefusal(e))
    })
    .finally(() => {
      saving.value = false
    })
}

// The API gives no response body, so the reason comes from the status code.
function refusalReason(e: any) {
  switch (e?.response?.status) {
    case 409:
      return 'Cannot delete this Edition: it is the current one or has talks, workshops or agenda days'
    case 404:
      return 'This Edition no longer exists'
    default:
      return 'Failed to delete Edition'
  }
}

function confirmDelete(item: EditionDto) {
  confirmDialog.value?.open(`Delete Edition "${item.name}"?`).then((confirmed: boolean) => {
    if (!confirmed) return
    // throwOnError: the generated client otherwise resolves with the error, which would look like a success.
    deleteEdition({ path: { id: item.id! }, throwOnError: true })
      .then(() => {
        Notify.success('Edition deleted')
        reload()
      })
      .catch((e) => {
        console.error(e)
        Notify.error(refusalReason(e))
        // A 404 means the list is stale.
        if (e?.response?.status === 404) reload()
      })
  })
}

onMounted(reload)
defineExpose({ confirmDelete, reload, editions, openCreate, openEdit, save, form, dialog })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>Editions</span>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Edition</v-btn>
      </v-card-title>

      <v-data-table :headers="headers" :items="editions" item-value="id" :items-per-page="100">
        <template #item.name="{ item }">
          {{ item.name }}
          <v-chip v-if="item.current" color="primary" size="small" label class="ml-2">Current</v-chip>
        </template>
        <template #item.conference="{ item }">
          {{ formatDateTime(item.conferenceStart!) }} – {{ formatDate(item.lastDay!) }}
        </template>
        <template #item.c4p="{ item }">
          <template v-if="item.c4pStart && item.c4pEnd">
            {{ formatDateTime(item.c4pStart) }} – {{ formatDateTime(item.c4pEnd) }}
          </template>
          <template v-else>No period</template>
          <v-chip :color="c4pOpen(item) ? 'success' : 'grey'" size="small" label class="ml-2">
            {{ c4pOpen(item) ? 'C4P open' : 'C4P closed' }}
          </v-chip>
        </template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" @click.stop="openEdit(item)" />
          <v-btn icon="mdi-delete-outline" variant="text" size="small" @click.stop="confirmDelete(item)" />
        </template>
      </v-data-table>

      <DialogConfirm ref="confirmDialog" />
    </v-card>

    <v-dialog v-model="dialog" max-width="600px" :persistent="saving">
      <v-card>
        <v-card-title>{{ editing ? 'Edit Edition' : 'Add Edition' }}</v-card-title>
        <v-card-text>
          <v-text-field v-if="!editing" v-model.number="form.year" type="number" label="Year" :rules="[requiredRule]" />
          <v-text-field v-model="form.name" label="Name" :rules="[requiredRule]" />
          <v-text-field
            v-model="form.conferenceStart"
            type="datetime-local"
            label="Conference start (Europe/Warsaw)"
            :rules="[requiredRule]"
          />
          <v-text-field v-model="form.lastDay" type="date" label="Last day" :rules="[requiredRule]" />
          <v-text-field
            v-model="form.c4pStart"
            type="datetime-local"
            label="C4P start (Europe/Warsaw)"
            hint="Leave both C4P dates empty for no C4P period"
            persistent-hint
          />
          <v-text-field v-model="form.c4pEnd" type="datetime-local" label="C4P end (Europe/Warsaw)" />
          <v-alert v-if="problem" type="warning" variant="tonal" density="compact">{{ problem }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" :disabled="saving" @click="dialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" :disabled="!!problem" :loading="saving" @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
