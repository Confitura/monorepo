<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { getAllNewsEntries, createNewsEntry, updateNewsEntry, deleteNewsEntry } from '@/utils/api.ts'
import { fromWarsawInput, toWarsawInput } from '@/utils/warsaw-time'
import type { NewsEntryDto } from '@/client'

const theme = useTheme()
const mdTheme = computed(() => (theme.current.value.dark ? 'dark' : 'light'))

definePage({
  meta: {
    title: 'News',
    icon: 'mdi-newspaper-variant-outline',
  },
})

const entries = ref<NewsEntryDto[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

// --- Create/edit dialog ---
const dialog = ref(false)
const editing = ref(false)
const form = ref<NewsEntryDto>({ title: '', slug: '', body: '', published: false })
// The date as typed in the datetime-local field: Warsaw wall-clock time, 'YYYY-MM-DDTHH:mm'.
const dateInput = ref('')
const savedSlug = ref('')
const requiredRule = (value: string) => !!value?.trim() || 'This field is required'
const valid = computed(() => !!form.value.title?.trim() && !!dateInput.value)
// Changing the slug of an existing entry breaks the links already shared.
const slugChanged = computed(
  () => editing.value && !!form.value.slug?.trim() && form.value.slug !== savedSlug.value,
)

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

function openCreate() {
  editing.value = false
  form.value = { title: '', slug: '', body: '', published: false }
  dateInput.value = toWarsawInput(new Date().toISOString())
  savedSlug.value = ''
  dialog.value = true
}

function openEdit(item: NewsEntryDto) {
  editing.value = true
  form.value = { ...item }
  dateInput.value = toWarsawInput(item.date!)
  savedSlug.value = item.slug ?? ''
  dialog.value = true
}

function save() {
  if (!valid.value) return
  const body = {
    title: form.value.title,
    // Left empty, the server generates the slug on create and keeps the current one on update.
    slug: form.value.slug?.trim() || undefined,
    body: form.value.body,
    date: fromWarsawInput(dateInput.value),
    published: form.value.published,
  }
  const request = editing.value
    ? updateNewsEntry({ path: { id: form.value.id! }, body })
    : createNewsEntry({ body })
  request
    .then(() => {
      Notify.success(editing.value ? 'News entry updated' : 'News entry created')
      dialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to save news entry (is the slug already in use?)')
    })
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
defineExpose({ confirmDelete, reload, entries, openCreate, openEdit, save, form, dateInput, editing })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>News</span>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add news entry</v-btn>
      </v-card-title>

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
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" @click.stop="openEdit(item)" />
          <v-btn icon="mdi-delete-outline" variant="text" size="small" @click.stop="confirmDelete(item)" />
        </template>
      </v-data-table>

      <DialogConfirm ref="confirmDialog" />
    </v-card>

    <v-dialog v-model="dialog" max-width="1000px">
      <v-card>
        <v-card-title>{{ editing ? 'Edit news entry' : 'Add news entry' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="form.title" label="Title" :rules="[requiredRule]" />
          <v-text-field
            v-model="form.slug"
            label="Slug"
            :hint="editing ? undefined : 'Leave empty to generate it from the title'"
            :persistent-hint="!editing"
          />
          <v-alert v-if="slugChanged" type="warning" variant="tonal" density="compact" class="mb-4">
            Changing the slug changes the entry's address: shared links will stop working.
          </v-alert>
          <v-text-field
            v-model="dateInput"
            type="datetime-local"
            label="Date and time (Europe/Warsaw)"
            hint="The entry goes public at this moment, if published"
            persistent-hint
            :rules="[requiredRule]"
          />
          <v-switch v-model="form.published" label="Published" color="primary" />
          <MdEditor v-model="form.body" :theme="mdTheme" language="en-US" style="height: 400px" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" :disabled="!valid" @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
