<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type { DataTableHeaders } from '@/plugins/vuetify'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import {
  getAllPartners,
  createPartner,
  updatePartner,
  deletePartner,
  storePartnerLogo,
} from '@/utils/api.ts'
import type { PartnerDto } from '@/client'

const theme = useTheme()
const mdTheme = computed(() => (theme.current.value.dark ? 'dark' : 'light'))

definePage({
  meta: {
    title: 'Partners',
    icon: 'mdi-handshake-outline',
  },
})

const TIERS = ['platinum', 'gold', 'silver', 'media', 'path', 'bronze', 'tech']
const ORIENTATIONS = ['horizontal', 'vertical', 'box']

const partners = ref<PartnerDto[]>([])
const confirmDialog = useTemplateRef('confirmDialog')

const dialog = ref(false)
const editing = ref(false)
const form = ref<PartnerDto>({
  slug: '',
  name: '',
  type: 'gold',
  www: '',
  description: '',
  orientation: 'horizontal',
  published: true,
})
const logoFile = ref<File | null>(null)

const headers: DataTableHeaders = [
  { title: 'Logo', key: 'logo', sortable: false },
  { title: 'Name', key: 'name' },
  { title: 'Tier', key: 'type' },
  { title: 'Published', key: 'published' },
  { title: 'Actions', key: 'actions', sortable: false },
]

const requiredRule = (value: string) => !!value?.trim() || 'This field is required'
const valid = computed(() => !!form.value.name?.trim() && !!form.value.type)

function reload() {
  getAllPartners()
    .then((res) => {
      partners.value = res.data ?? []
    })
    .catch((e) => console.error(e))
}

function openCreate() {
  editing.value = false
  form.value = { slug: '', name: '', type: 'gold', www: '', description: '', orientation: 'horizontal', published: true }
  logoFile.value = null
  dialog.value = true
}

function openEdit(item: PartnerDto) {
  editing.value = true
  form.value = { ...item }
  logoFile.value = null
  dialog.value = true
}

async function save() {
  if (!valid.value) return
  const body = {
    slug: form.value.slug,
    name: form.value.name,
    type: form.value.type,
    www: form.value.www,
    description: form.value.description,
    orientation: form.value.orientation,
    published: form.value.published,
  }
  try {
    const res = editing.value
      ? await updatePartner({ path: { id: form.value.id! }, body })
      : await createPartner({ body })
    const id = res.data?.id ?? form.value.id
    if (logoFile.value && id) {
      await storePartnerLogo({ path: { id }, body: { file: logoFile.value } })
    }
    Notify.success(editing.value ? 'Partner updated' : 'Partner created')
    dialog.value = false
    reload()
  } catch (e: unknown) {
    console.error(e)
    const status = (e as { status?: number })?.status
    Notify.error(status === 413 ? 'Logo file is too large' : 'Failed to save partner')
  }
}

function confirmDelete(item: PartnerDto) {
  confirmDialog.value?.open(`Delete partner "${item.name}"?`).then((confirmed: boolean) => {
    if (!confirmed) return
    deletePartner({ path: { id: item.id! } })
      .then(() => {
        Notify.success('Partner deleted')
        reload()
      })
      .catch((e) => {
        console.error(e)
        Notify.error('Failed to delete partner')
      })
  })
}

onMounted(reload)
defineExpose({ openCreate, openEdit, save, confirmDelete, reload, form, editing, logoFile, partners })
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>Partners</span>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add partner</v-btn>
      </v-card-title>

      <v-data-table
        :headers="headers"
        :items="partners"
        item-value="id"
        :items-per-page="100"
      >
        <template #item.logo="{ item }">
          <img v-if="item.logo" :src="item.logo" :alt="item.name" height="28" style="max-width: 90px" />
        </template>
        <template #item.published="{ item }">
          <v-icon :color="item.published ? 'success' : 'grey'">
            {{ item.published ? 'mdi-eye' : 'mdi-eye-off' }}
          </v-icon>
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
        <v-card-title>{{ editing ? 'Edit partner' : 'Add partner' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="form.name" label="Name" :rules="[requiredRule]" />
          <v-text-field
            v-model="form.slug"
            label="URL slug"
            hint="Used in the partner page URL, e.g. /partners/xtb"
            persistent-hint
          />
          <v-select v-model="form.type" :items="TIERS" label="Tier" :rules="[requiredRule]" />
          <v-text-field v-model="form.www" label="Website" />
          <v-select v-model="form.orientation" :items="ORIENTATIONS" label="Logo orientation" />
          <v-switch v-model="form.published" label="Published" color="primary" />
          <v-file-input
            v-model="logoFile"
            label="Logo"
            accept="image/png,image/jpeg,image/gif,image/webp,image/svg+xml"
            prepend-icon="mdi-image-outline"
          />
          <p class="text-caption text-medium-emphasis mb-2">
            Tip: optimise SVG logos with
            <a href="https://svgomg.net/" target="_blank" rel="noopener noreferrer">svgomg.net</a>
            before uploading.
          </p>
          <img
            v-if="editing && form.logo && !logoFile"
            :src="form.logo"
            :alt="form.name"
            height="40"
            class="mb-2"
          />
          <MdEditor
            v-model="form.description"
            :theme="mdTheme"
            language="en-US"
            style="height: 320px"
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
