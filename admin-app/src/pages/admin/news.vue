<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { getAllNewsEntries, deleteNewsEntry } from '@/utils/api.ts'
import type { NewsEntryDto } from '@/client'

definePage({
  meta: {
    title: 'News',
    icon: 'mdi-newspaper-variant-outline',
  },
})

const entries = ref<NewsEntryDto[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

const headers: DataTableHeaders = [
  { title: 'Title', key: 'title', sortable: false },
  { title: 'Date', key: 'date', sortable: false },
  { title: 'State', key: 'state', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false },
]

const dateFormat = new Intl.DateTimeFormat('en-GB', {
  timeZone: 'Europe/Warsaw',
  dateStyle: 'medium',
  timeStyle: 'short',
})

function formatDate(date?: string) {
  return date ? dateFormat.format(new Date(date)) : ''
}

// An entry is public once it is published and its date has passed (see News Entry in CONTEXT.md).
function stateOf(item: NewsEntryDto) {
  if (!item.published) return { label: 'Draft', color: 'grey' }
  if (new Date(item.date!).getTime() > Date.now()) return { label: 'Scheduled', color: 'warning' }
  return { label: 'Public', color: 'success' }
}

function reload() {
  getAllNewsEntries()
    .then((res) => {
      entries.value = res.data ?? []
    })
    .catch((e) => console.error(e))
}

function confirmDelete(item: NewsEntryDto) {
  confirmDialog.value?.open(`Delete news entry "${item.title}"?`).then((confirmed: boolean) => {
    if (!confirmed) return
    deleteNewsEntry({ path: { id: item.id! } })
      .then(() => {
        Notify.success('News entry deleted')
        reload()
      })
      .catch((e) => {
        console.error(e)
        Notify.error('Failed to delete news entry')
      })
  })
}

onMounted(reload)
defineExpose({ confirmDelete, reload, entries })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title>News</v-card-title>

      <v-data-table
        :headers="headers"
        :items="entries"
        item-value="id"
        :items-per-page="100"
      >
        <template #item.date="{ item }">{{ formatDate(item.date) }}</template>
        <template #item.state="{ item }">
          <v-chip :color="stateOf(item).color" size="small" label>{{ stateOf(item).label }}</v-chip>
        </template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-delete-outline" variant="text" size="small" @click.stop="confirmDelete(item)" />
        </template>
      </v-data-table>

      <DialogConfirm ref="confirmDialog" />
    </v-card>
  </v-container>
</template>
