<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type {DataTableHeaders} from '@/plugins/vuetify'
import {adminPresentationApi, presentationApi} from "@/utils/api.ts";
import type {
  FullPresentation,
  ViewPresentationRate
} from "@/utils/api-axios-client";


definePage({
  meta: {
    title: 'Ratings & Reviews',
    icon: 'mdi-animation',
  },
})

const search = ref('')



const headers: DataTableHeaders = [
  {title: 'Speakers', key: 'speakers'},
  {title: 'Title', key: 'title'},
  {title: 'count total', key: 'ratingCount'},
  {title: 'avg', key: 'avgRating'},
  {title: 'Awesome', key: 'ratingCountAwesome'},
  {title: 'Great', key: 'ratingCountGreat'},
  {title: 'ItWasFine', key: 'ratingCountItWasFine'},
  {title: 'Bad', key: 'ratingCountBad'},
  {title: 'Terrible', key: 'ratingCountTerrible'},
  {title: 'actions', key: 'actions'},
]
const presentations = ref<ViewPresentationRate[]>([])


function reloadRates() {
  adminPresentationApi.rates()
    .then(res => res.data)
    .then((data: Array<ViewPresentationRate>) => presentations.value = data)
    .catch(e => console.error(e))
}

onMounted(() => {
  reloadRates();
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
            items-per-page="100"
            :headers="headers"
            :items="presentations"
            item-value="presentationId"
            :filter-keys="['title','speakers']"
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

                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                      icon="mdi-eye-outline"
                      v-bind="props"
                      :href="`/admin/presentation-preview/${item.presentationId}`"
                      target="_blank"
                    />
                  </template>
                  <span>Preview</span>
                </v-tooltip>
              </v-defaults-provider>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
