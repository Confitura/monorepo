<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type {DataTableHeaders} from '@/plugins/vuetify'
import {pagesApi} from "@/utils/api.ts";
import axios from "axios";

definePage({
  meta: {
    title: 'Pages',
    icon: 'mdi-file-document-outline',
  },
})

const search = ref('')
const confirmDialog = useTemplateRef('confirmDialog')
const editDialog = ref(false)
const createDialog = ref(false)

const headers: DataTableHeaders = [
  {title: 'ID', key: 'id'},
  {title: 'Actions', key: 'actions', sortable: false},
]

const pages = ref<{id: string}[]>([])
const currentPage = ref({
  id: '',
  content: ''
})
const newPage = ref({
  id: '',
  content: ''
})

const formValid = ref(false)
const requiredRule = (value: string) => !!value.trim() || 'This field is required'

function reloadPages() {
  pagesApi.getPages()
      .then(res => res.data)
      .then(data => {
        // Transform array of strings to array of objects with id property
        pages.value = data.map(id => ({ id }))
      })
      .catch(e => console.error(e))
}

function showEditDialog(item: {id: string}) {
  pagesApi.getPage(item.id)
      .then(res => res.data)
      .then(content => {
        currentPage.value = {
          id: item.id,
          content: content
        }
        editDialog.value = true
      })
      .catch(e => {
        console.error(e)
        Notify.error(`Failed to load page ${item.id}`)
      })
}

function showCreateDialog() {
  newPage.value = {
    id: '',
    content: ''
  }
  createDialog.value = true
}

function showDeleteDialog(item: {id: string}) {
  confirmDialog.value
      ?.open(`Are you sure you want to DELETE page ${item.id}?`)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          deletePage(item.id)
        }
      })
}

function updatePage() {
  if (!formValid.value) return

  pagesApi.updatePage(currentPage.value.id, {content: currentPage.value.content})
      .then(() => {
        Notify.success(`Page ${currentPage.value.id} updated`)
        editDialog.value = false
        reloadPages()
      })
      .catch(error => {
        console.error('Update failed:', error)
        Notify.error(`Failed to update page ${currentPage.value.id}`)
      })
}

function createPage() {
  if (!formValid.value) return

  pagesApi.createPage(newPage.value.id, {content: newPage.value.content})
      .then(() => {
        Notify.success(`Page ${newPage.value.id} created`)
        createDialog.value = false
        reloadPages()
      })
      .catch(error => {
        console.error('Creation failed:', error)
        Notify.error(`Failed to create page ${newPage.value.id}`)
      })
}

function deletePage(pageId: string) {

  pagesApi.deletePage(pageId)
      .then(() => {
        Notify.success(`Page ${pageId} deleted`)
        reloadPages()
      })
      .catch(error => {
        console.error('Deletion failed:', error)
        Notify.error(`Failed to delete page ${pageId}`)
      })
}

onMounted(() => {
  reloadPages();
});
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <v-card>
          <teleport to="#app-bar">
            <v-text-field
                v-model="search"
                prepend-inner-icon="mdi-magnify"
                label="Search"
                single-line
                hide-details
                density="compact"
                class="mr-2"
                rounded="xl"
                flat
                variant="solo"
                style="width: 250px"
            />
          </teleport>

          <v-card-title class="d-flex justify-space-between align-center">
            <span>Pages</span>
            <v-btn
                color="primary"
                prepend-icon="mdi-plus"
                @click="showCreateDialog"
            >
              Create Page
            </v-btn>
          </v-card-title>

          <v-data-table
              ref="table"
              :headers="headers"
              :items="pages"
              item-value="id"
              :search="search"
          >
            <template #item.actions="{ item }">
              <v-defaults-provider
                  :defaults="{
                  VBtn: {
                    size: 20,
                    rounded: 'sm',
                    variant: 'text',
                    class: 'ml-1',
                    color: '',
                  },
                  VIcon: {
                    size: 20,
                  },
                }"
              >
                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-pencil-outline"
                        v-bind="props"
                        @click.stop="showEditDialog(item)"
                    />
                  </template>
                  <span>Edit</span>
                </v-tooltip>

                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-delete-outline"
                        v-bind="props"
                        @click.stop="showDeleteDialog(item)"
                    />
                  </template>
                  <span>Delete</span>
                </v-tooltip>
              </v-defaults-provider>
            </template>
          </v-data-table>
          <DialogConfirm ref="confirmDialog"/>
        </v-card>
      </v-col>
    </v-row>

    <!-- Edit Dialog -->
    <v-dialog v-model="editDialog" max-width="800px">
      <v-card>
        <v-card-title>Edit Page: {{ currentPage.id }}</v-card-title>
        <v-card-text>
          <v-form v-model="formValid">
            <v-textarea
                v-model="currentPage.content"
                label="Content"
                outlined
                rows="15"
                required
                :rules="[requiredRule]"
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text" @click="editDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="updatePage" :disabled="!formValid">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Create Dialog -->
    <v-dialog v-model="createDialog" max-width="800px">
      <v-card>
        <v-card-title>Create New Page</v-card-title>
        <v-card-text>
          <v-form v-model="formValid">
            <v-text-field
                v-model="newPage.id"
                label="Page ID"
                outlined
                required
                :rules="[requiredRule]"
            ></v-text-field>

            <v-textarea
                v-model="newPage.content"
                label="Content"
                outlined
                rows="15"
                required
                :rules="[requiredRule]"
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text" @click="createDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="createPage" :disabled="!formValid">Create</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
