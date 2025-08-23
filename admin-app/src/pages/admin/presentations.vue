<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type {DataTableHeaders} from '@/plugins/vuetify'
import {adminPresentationApi, presentationApi} from "@/utils/api.ts";
import type {FullPresentation} from "@/utils/api-axios-client";


definePage({
  meta: {
    title: 'Presentations & Workshops',
    icon: 'mdi-animation',
  },
})

const search = ref('')
const confirmDialog = useTemplateRef('confirmDialog')

function showDialogApprove(presentation: FullPresentation) {
  confirmDialog.value
    ?.open(`Are you sure you want to APPROVE ${presentation.title}?`)
    .then(async (confirmed: boolean) => {
      if (confirmed) {
        adminPresentationApi.accept(presentation.id!)
          .then(_ => Notify.success(`Approved ${presentation.title}`))
          .then(_ => reloadPresentations())
          .catch(_ => Notify.error('Failed to approve'))
      }
    })
}

function showDialogReject(presentation: FullPresentation) {
  confirmDialog.value
    ?.open(`Are you sure you want to REJECT ${presentation.title}?`)
    .then(async (confirmed: boolean) => {
      if (confirmed) {
        adminPresentationApi.reject(presentation.id!)
          .then(_ => Notify.success(`Rejected  ${presentation.title}`))
          .then(_ => reloadPresentations())
          .catch(_ => Notify.error('Failed to reject'))
      }
    })
}

const headers: DataTableHeaders = [
  {title: 'Title', key: 'title'},
  {title: 'Speakers', key: 'flatSpeakers'},
  {title: 'Level', key: 'level'},
  {title: 'Language', key: 'language'},
  {title: 'Tags', key: 'flatTags'},
  {title: 'Workshop', key: 'isWorkshop'},
  {title: 'Free', key: 'isFree'},
  {title: 'Expected price [PLN]', key: 'expectedPrice'},
  {title: 'Duration [min]', key: 'durationInMinutes'},
  {title: 'Max group size', key: 'maxGroupSize'},
  {title: 'Status', key: 'status'},
  {title: 'Actions', key: 'actions', sortable: false},
]
const presentations = ref<FullPresentation[]>([])


function reloadPresentations() {
  presentationApi.getAllPresentations()
    .then(res => res.data)
    .then((data: Array<FullPresentation>) => presentations.value = data)
    .catch(e => console.error(e))
}

onMounted(() => {
  reloadPresentations();
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
          <v-data-table
            ref="table"
            :headers="headers"
            :items="presentations"
            item-value="name"
            :filter-keys="['title','flatSpeakers','flatTags']"
            :search="search"
          >
            <template #item.speakers="{ item }">
              {{ item.speakers?.map(speaker => speaker.name).join(', ') || '' }}
            </template>
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
                <v-tooltip location="top" v-if="item.status !== 'accepted'">
                  <template #activator="{ props }">
                    <v-btn
                      icon="mdi-check-outline"
                      v-bind="props"
                      @click.stop="showDialogApprove(item)"
                    />
                  </template>
                  <span>Approve</span>
                </v-tooltip>

                <v-tooltip location="top" v-else>
                  <template #activator="{ props }">
                    <v-btn
                      icon="mdi-close"
                      v-bind="props"
                      @click.stop="showDialogReject(item)"
                    />
                  </template>
                  <span>Reject</span>
                </v-tooltip>

                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                      v-if="item.isWorkshop"
                      icon="mdi-pencil-outline"
                      v-bind="props"
                      :to="`/workshop-form/${item.id}`"
                    />
                    <v-btn
                      v-else
                      icon="mdi-pencil-outline"
                      v-bind="props"
                      :to="`/presentation-form/${item.id}`"
                    />
                  </template>
                  <span>Edit</span>
                </v-tooltip>

                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                      icon="mdi-eye-outline"
                      v-bind="props"
                      :href="`/admin/presentation-preview/${item.id}`"
                      target="_blank"
                    />
                  </template>
                  <span>Preview</span>
                </v-tooltip>
              </v-defaults-provider>
            </template>
          </v-data-table>
          <DialogConfirm ref="confirmDialog"/>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
