<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { getAllEditions, createEdition, updateEdition, deleteEdition, makeEditionCurrent } from '@/utils/api.ts'
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
// The form shows minutes only. On edit, the stored instants are kept with their form text, so a date the
// admin did not touch is sent back exactly as stored instead of rounded down to the minute.
let stored: Record<string, { instant: string; text: string }> = {}
const dateTimeFields = ['conferenceStart', 'c4pStart', 'c4pEnd'] as const
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
  getAllEditions()
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
  stored = {}
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
  stored = {}
  for (const field of dateTimeFields) {
    if (item[field]) stored[field] = { instant: item[field], text: form.value[field] }
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

// The stored instant while the field still shows its loaded text, otherwise the edited text read as Warsaw time.
const instantOf = (field: (typeof dateTimeFields)[number]) => {
  const text = form.value[field]
  if (!text) return undefined
  const original = stored[field]
  return original?.text === text ? original.instant : fromWarsawInput(text)
}

function save() {
  if (problem.value || saving.value) return
  const f = form.value
  const body = {
    // An update must repeat the stored year.
    year: f.year,
    name: f.name.trim(),
    conferenceStart: instantOf('conferenceStart'),
    lastDay: f.lastDay,
    // The update replaces the whole Edition: leaving both out closes the C4P.
    c4pStart: instantOf('c4pStart'),
    c4pEnd: instantOf('c4pEnd'),
  }
  saving.value = true
  const request = editing.value
    ? updateEdition({ path: { id: f.id }, body })
    : createEdition({ body })
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
    deleteEdition({ path: { id: item.id! } })
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

// --- Make current ---
const switchDialog = ref(false)
const switchTo = ref<EditionDto>()
const currentName = computed(() => editions.value.find((e) => e.current)?.name ?? 'the current Edition')
// A switch in flight: blocks a second one and closing the dialog until it completes.
const switching = ref(false)

function openMakeCurrent(item: EditionDto) {
  switchTo.value = item
  switchDialog.value = true
}

function makeCurrent() {
  if (switching.value || !switchTo.value) return
  const edition = switchTo.value
  switching.value = true
  makeEditionCurrent({ path: { id: edition.id! } })
    .then(() => {
      Notify.success(`${edition.name} is now the current Edition`)
      switchDialog.value = false
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to make the Edition current')
    })
    .finally(() => {
      switching.value = false
      // Even a failed switch may mean the list is stale, e.g. another admin switched first.
      reload()
    })
}

onMounted(reload)
defineExpose({ confirmDelete, reload, editions, openCreate, openEdit, save, form, dialog, openMakeCurrent, makeCurrent, switchDialog })
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
          <v-btn v-if="!item.current" variant="text" size="small" @click.stop="openMakeCurrent(item)">Make current</v-btn>
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

    <v-dialog v-model="switchDialog" max-width="600px" :persistent="switching">
      <v-card>
        <v-card-title>Make {{ switchTo?.name }} current?</v-card-title>
        <v-card-text>
          <ol class="ml-4">
            <li>The webpage dumps move to <code>edition-{{ switchTo?.year }}</code>.</li>
            <li>The dumps of {{ currentName }} freeze as its Archive.</li>
            <li>The votes of {{ currentName }} lose the voters' IP addresses. This cannot be undone.</li>
            <li>
              Change admins and volunteers for the new year only after switching, or they disappear from the
              previous Archive.
            </li>
            <li>
              The webpage keeps showing the previous Archive until its config is bumped (see
              <code>webpage/NEXT-EDITION.md</code>).
            </li>
          </ol>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" :disabled="switching" @click="switchDialog = false">Cancel</v-btn>
          <v-btn color="error" variant="text" :loading="switching" @click="makeCurrent">Make current</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
