<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { getAllEditions, deleteEdition } from '@/utils/api.ts'
import type { EditionDto } from '@/client'

definePage({
  meta: {
    title: 'Editions',
    icon: 'mdi-calendar-star',
  },
})

const editions = ref<EditionDto[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

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
defineExpose({ confirmDelete, reload, editions })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title>Editions</v-card-title>

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
          <v-btn icon="mdi-delete-outline" variant="text" size="small" @click.stop="confirmDelete(item)" />
        </template>
      </v-data-table>

      <DialogConfirm ref="confirmDialog" />
    </v-card>
  </v-container>
</template>
