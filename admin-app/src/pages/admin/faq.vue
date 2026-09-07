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
  getFaqCategories,
  createFaqCategory,
  updateFaqCategory,
  reorderFaqCategories,
  mergeFaqCategories,
  deleteFaqCategory,
} from '@/utils/api.ts'
import type { FaqEntryDto, FaqCategoryDto } from '@/client'

const theme = useTheme()
const mdTheme = computed(() => (theme.current.value.dark ? 'dark' : 'light'))

definePage({
  meta: {
    title: 'FAQ',
    icon: 'mdi-frequently-asked-questions',
  },
})

// A category (dictionary row) together with the entries that belong to it.
interface Group {
  category: FaqCategoryDto
  items: FaqEntryDto[]
}

const groups = ref<Group[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

const categoryNames = computed(() => groups.value.map((g) => g.category.name ?? ''))
const entryCount = computed(() => groups.value.reduce((n, g) => n + g.items.length, 0))

// --- Entry create/edit dialog ---
const dialog = ref(false)
const editing = ref(false)
const form = ref<FaqEntryDto>({ category: '', question: '', answer: '', published: true })
const requiredRule = (value: string) => !!value?.trim() || 'This field is required'
const valid = computed(() => !!form.value.category?.trim() && !!form.value.question?.trim())

// --- Category dialogs ---
const categoryDialog = ref(false)
const categoryName = ref('')

const renameDialog = ref(false)
const renameTarget = ref<FaqCategoryDto | null>(null)
const renameTo = ref('')

const mergeDialog = ref(false)
const mergeFrom = ref<FaqCategoryDto | null>(null)
const mergeTo = ref<string | null>(null)
const mergeTargets = computed(() =>
  groups.value.map((g) => g.category).filter((c) => c.id !== mergeFrom.value?.id),
)

// Build groups from the category dictionary (source of truth for order + visibility),
// attaching each category's entries.
function toGroups(entries: FaqEntryDto[], categories: FaqCategoryDto[]): Group[] {
  const byName = new Map<string, FaqEntryDto[]>()
  for (const entry of entries) {
    const key = (entry.category ?? '').toLowerCase()
    if (!byName.has(key)) byName.set(key, [])
    byName.get(key)!.push(entry)
  }
  return categories.map((category) => ({
    category,
    items: byName.get((category.name ?? '').toLowerCase()) ?? [],
  }))
}

function reload() {
  Promise.all([getAllFaqEntries(), getFaqCategories()])
    .then(([entriesRes, categoriesRes]) => {
      groups.value = toGroups(entriesRes.data ?? [], categoriesRes.data ?? [])
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

// Persist the order of entries within one category after a drag.
function persistEntryOrder(group: Group) {
  const ids = group.items.map((i) => i.id!)
  reorderFaqEntries({ body: { ids } })
    .then(() => Notify.success('Order saved'))
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to save order')
    })
}

// --- Category operations ---

function openCreateCategory() {
  categoryName.value = ''
  categoryDialog.value = true
}

function createCategoryConfirm() {
  const name = categoryName.value.trim()
  if (!name) return
  createFaqCategory({ body: { name } })
    .then(() => {
      Notify.success('Category created')
      categoryDialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to create category (does it already exist?)')
    })
}

function openRename(category: FaqCategoryDto) {
  renameTarget.value = category
  renameTo.value = category.name ?? ''
  renameDialog.value = true
}

function renameConfirm() {
  const to = renameTo.value.trim()
  if (!to || !renameTarget.value?.id || to === renameTarget.value.name) {
    renameDialog.value = false
    return
  }
  updateFaqCategory({ path: { id: renameTarget.value.id }, body: { name: to } })
    .then(() => {
      Notify.success('Category renamed')
      renameDialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to rename category (name already in use?)')
    })
}

function toggleCategoryPublished(category: FaqCategoryDto) {
  updateFaqCategory({ path: { id: category.id! }, body: { published: !category.published } })
    .then(() => reload())
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to change visibility')
    })
}

// Move a category up/down and persist the new order of all categories.
function moveCategory(index: number, direction: -1 | 1) {
  const target = index + direction
  if (target < 0 || target >= groups.value.length) return
  const reordered = [...groups.value]
  ;[reordered[index], reordered[target]] = [reordered[target], reordered[index]]
  groups.value = reordered
  const ids = reordered.map((g) => g.category.id!)
  reorderFaqCategories({ body: { ids } })
    .then(() => Notify.success('Order saved'))
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to save order')
    })
}

function openMerge(category: FaqCategoryDto) {
  mergeFrom.value = category
  mergeTo.value = null
  mergeDialog.value = true
}

function mergeConfirm() {
  if (!mergeFrom.value?.id || !mergeTo.value) return
  mergeFaqCategories({ body: { from: mergeFrom.value.id, to: mergeTo.value } })
    .then(() => {
      Notify.success('Categories merged')
      mergeDialog.value = false
      reload()
    })
    .catch((e) => {
      console.error(e)
      Notify.error('Failed to merge categories')
    })
}

function confirmDeleteCategory(group: Group) {
  if (group.items.length > 0) {
    Notify.error('Move or merge its questions first')
    return
  }
  confirmDialog.value
    ?.open(`Delete empty category "${group.category.name}"?`)
    .then((confirmed: boolean) => {
      if (!confirmed) return
      deleteFaqCategory({ path: { id: group.category.id! } })
        .then(() => {
          Notify.success('Category deleted')
          reload()
        })
        .catch((e) => {
          console.error(e)
          Notify.error('Failed to delete category')
        })
    })
}

onMounted(reload)
// Exposed for the parent and for interaction tests.
defineExpose({
  reload,
  groups,
  categoryNames,
  openCreate,
  openEdit,
  save,
  confirmDelete,
  persistEntryOrder,
  form,
  editing,
  openCreateCategory,
  createCategoryConfirm,
  categoryName,
  openRename,
  renameConfirm,
  renameTarget,
  renameTo,
  toggleCategoryPublished,
  moveCategory,
  openMerge,
  mergeConfirm,
  mergeFrom,
  mergeTo,
  confirmDeleteCategory,
})
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>FAQ</span>
        <div class="d-flex ga-2">
          <v-btn variant="tonal" prepend-icon="mdi-folder-plus-outline" @click="openCreateCategory">
            Add category
          </v-btn>
          <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add question</v-btn>
        </div>
      </v-card-title>

      <v-card-text>
        <p v-if="groups.length === 0" class="text-medium-emphasis">
          No categories yet. Add a category or a question.
        </p>

        <div v-for="(group, index) in groups" :key="group.category.id" class="mb-6">
          <div class="d-flex align-center mb-2">
            <h3 class="text-h6">{{ group.category.name }}</h3>
            <v-chip v-if="!group.category.published" size="x-small" color="warning" class="ml-2">
              hidden
            </v-chip>
            <span class="text-medium-emphasis text-caption ml-2">
              {{ group.items.length }} question(s)
            </span>
            <v-spacer />
            <v-btn
              icon="mdi-arrow-up"
              variant="text"
              size="x-small"
              title="Move up"
              :disabled="index === 0"
              @click="moveCategory(index, -1)"
            />
            <v-btn
              icon="mdi-arrow-down"
              variant="text"
              size="x-small"
              title="Move down"
              :disabled="index === groups.length - 1"
              @click="moveCategory(index, 1)"
            />
            <v-btn
              :icon="group.category.published ? 'mdi-eye-outline' : 'mdi-eye-off-outline'"
              variant="text"
              size="x-small"
              :title="group.category.published ? 'Hide category' : 'Show category'"
              @click="toggleCategoryPublished(group.category)"
            />
            <v-btn
              icon="mdi-pencil-outline"
              variant="text"
              size="x-small"
              title="Rename category"
              @click="openRename(group.category)"
            />
            <v-btn
              icon="mdi-call-merge"
              variant="text"
              size="x-small"
              title="Merge into another category"
              @click="openMerge(group.category)"
            />
            <v-btn
              icon="mdi-delete-outline"
              variant="text"
              size="x-small"
              title="Delete category (must be empty)"
              :disabled="group.items.length > 0"
              @click="confirmDeleteCategory(group)"
            />
          </div>
          <v-list density="comfortable" class="faq-list">
            <p v-if="group.items.length === 0" class="text-medium-emphasis text-caption ml-2">
              No questions in this category yet.
            </p>
            <draggable
              v-model="group.items"
              item-key="id"
              handle=".drag-handle"
              @end="persistEntryOrder(group)"
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
          <v-combobox
            v-model="form.category"
            :items="categoryNames"
            label="Category"
            hint="Pick an existing category or type a new one"
            persistent-hint
            :rules="[requiredRule]"
          />
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

    <v-dialog v-model="categoryDialog" max-width="500px">
      <v-card>
        <v-card-title>Add category</v-card-title>
        <v-card-text>
          <v-text-field v-model="categoryName" label="Category name" :rules="[requiredRule]" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" @click="categoryDialog = false">Cancel</v-btn>
          <v-btn
            color="blue-darken-1"
            variant="text"
            :disabled="!categoryName.trim()"
            @click="createCategoryConfirm"
          >
            Create
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="renameDialog" max-width="500px">
      <v-card>
        <v-card-title>Rename category</v-card-title>
        <v-card-text>
          <v-text-field v-model="renameTo" label="Category name" :rules="[requiredRule]" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" @click="renameDialog = false">Cancel</v-btn>
          <v-btn
            color="blue-darken-1"
            variant="text"
            :disabled="!renameTo.trim()"
            @click="renameConfirm"
          >
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="mergeDialog" max-width="500px">
      <v-card>
        <v-card-title>Merge category</v-card-title>
        <v-card-text>
          <p class="text-medium-emphasis mb-3">
            Move every question from "{{ mergeFrom?.name }}" into another category, then delete
            "{{ mergeFrom?.name }}".
          </p>
          <v-select
            v-model="mergeTo"
            :items="mergeTargets"
            item-title="name"
            item-value="id"
            label="Merge into"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue-darken-1" variant="text" @click="mergeDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" :disabled="!mergeTo" @click="mergeConfirm">
            Merge
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
