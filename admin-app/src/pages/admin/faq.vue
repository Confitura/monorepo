<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import draggable from 'vuedraggable'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import {
  getAllFaqEntries,
  createFaqEntry,
  updateFaqEntry,
  deleteFaqEntry,
  reorderFaqEntries,
} from '@/utils/api.ts'
import type { FaqEntryDto } from '@/client'

const theme = useTheme()
const mdTheme = computed(() => (theme.current.value.dark ? 'dark' : 'light'))

definePage({
  meta: {
    title: 'FAQ',
    icon: 'mdi-frequently-asked-questions',
  },
})

interface Group {
  category: string
  items: FaqEntryDto[]
}

const grouped = ref<Group[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

const dialog = ref(false)
const editing = ref(false)
const form = ref<FaqEntryDto>({ category: '', question: '', answer: '', published: true })

const requiredRule = (value: string) => !!value?.trim() || 'This field is required'
const valid = computed(() => !!form.value.category?.trim() && !!form.value.question?.trim())
const entryCount = computed(() => grouped.value.reduce((n, g) => n + g.items.length, 0))

function toGroups(entries: FaqEntryDto[]): Group[] {
  const map = new Map<string, FaqEntryDto[]>()
  for (const entry of entries) {
    const category = entry.category || 'General'
    if (!map.has(category)) map.set(category, [])
    map.get(category)!.push(entry)
  }
  return Array.from(map, ([category, items]) => ({ category, items }))
}

function reload() {
  getAllFaqEntries()
    .then((res) => {
      grouped.value = toGroups(res.data ?? [])
    })
    .catch((e) => console.error(e))
}

function openCreate() {
  editing.value = false
  form.value = { category: '', question: '', answer: '', published: true }
  dialog.value = true
}

function openEdit(item: FaqEntryDto) {
  editing.value = true
  form.value = { ...item }
  dialog.value = true
}

function save() {
  if (!valid.value) return
  const body = {
    category: form.value.category,
    question: form.value.question,
    answer: form.value.answer,
    published: form.value.published,
  }
  const request = editing.value
    ? updateFaqEntry({ path: { id: form.value.id! }, body })
    : createFaqEntry({ body: { ...body, displayOrder: entryCount.value } })
  request
    .then(() => {
      Notify.success(editing.value ? 'Question updated' : 'Question created')
      dialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to save question')
    })
}

function confirmDelete(item: FaqEntryDto) {
  confirmDialog.value?.open(`Delete question "${item.question}"?`).then((confirmed: boolean) => {
    if (!confirmed) return
    deleteFaqEntry({ path: { id: item.id! } })
      .then(() => {
        Notify.success('Question deleted')
        reload()
      })
      .catch((e) => {
        console.error(e)
        Notify.error('Failed to delete question')
      })
  })
}

// Persist the current order after a drag: categories in display order, entries in their order.
function persistOrder() {
  const ids = grouped.value.flatMap((g) => g.items.map((i) => i.id!))
  reorderFaqEntries({ body: { ids } })
    .then(() => Notify.success('Order saved'))
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to save order')
    })
}

onMounted(reload)
// Exposed for the parent and for interaction tests.
defineExpose({ persistOrder, openCreate, openEdit, save, confirmDelete, reload, form, editing, grouped })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>FAQ</span>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add question</v-btn>
      </v-card-title>

      <v-card-text>
        <p v-if="entryCount === 0" class="text-medium-emphasis">
          No FAQ entries yet. Add your first question.
        </p>

        <div v-for="group in grouped" :key="group.category" class="mb-6">
          <h3 class="text-h6 mb-2">{{ group.category }}</h3>
          <v-list density="comfortable" class="faq-list">
            <draggable
              v-model="group.items"
              item-key="id"
              handle=".drag-handle"
              @end="persistOrder"
            >
              <template #item="{ element }">
                <v-list-item class="faq-row" border rounded>
                  <template #prepend>
                    <v-icon class="drag-handle" style="cursor: grab">mdi-drag</v-icon>
                  </template>
                  <v-list-item-title>
                    {{ element.question }}
                    <v-chip v-if="!element.published" size="x-small" color="warning" class="ml-2">
                      hidden
                    </v-chip>
                  </v-list-item-title>
                  <template #append>
                    <v-btn
                      icon="mdi-pencil-outline"
                      variant="text"
                      size="small"
                      @click="openEdit(element)"
                    />
                    <v-btn
                      icon="mdi-delete-outline"
                      variant="text"
                      size="small"
                      @click="confirmDelete(element)"
                    />
                  </template>
                </v-list-item>
              </template>
            </draggable>
          </v-list>
        </div>
      </v-card-text>

      <DialogConfirm ref="confirmDialog" />
    </v-card>

    <v-dialog v-model="dialog" max-width="1000px">
      <v-card>
        <v-card-title>{{ editing ? 'Edit question' : 'Add question' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="form.category" label="Category" :rules="[requiredRule]" />
          <v-text-field v-model="form.question" label="Question" :rules="[requiredRule]" />
          <v-switch v-model="form.published" label="Published" color="primary" />
          <MdEditor
            v-model="form.answer"
            :theme="mdTheme"
            language="en-US"
            style="height: 400px"
          />
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
