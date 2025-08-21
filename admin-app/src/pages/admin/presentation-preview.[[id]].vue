<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { presentationApi } from '@/utils/api.ts'
import type { FullPresentation, User } from '@/utils/api-axios-client'

const route = useRoute()
const presentationId = route.params.id as string

const loading = ref(true)
const presentation = ref<FullPresentation | null>(null)
const cospeakers = ref<User[]>([])

async function loadData() {
  try {
    // There is no get-by-id in PresentationControllerApi, fetch all and find one
    const presList = await presentationApi.getAllPresentations()
    const items = presList.data || []
    presentation.value = (items as FullPresentation[]).find(p => p.id === presentationId) || null
  } catch (e) {
    console.error('Failed to load presentation', e)
  }
  try {
    // getCospeakers returns users with bio/photo etc.
    const cos = await presentationApi.getCospeakers(presentationId)
    cospeakers.value = Array.from(cos.data || [])
  } catch (e) {
    console.warn('Failed to load cospeakers', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card>
          <v-toolbar density="comfortable" color="primary" title="Presentation preview" />
          <v-card-text>
            <div v-if="loading" class="pa-6">
              <v-progress-circular indeterminate color="primary" />
            </div>
            <div v-else-if="!presentation" class="pa-6">
              <v-alert type="error" title="Not found" text="Presentation could not be loaded." />
            </div>
            <div v-else class="preview">
              <h2 class="mb-2">{{ presentation.title }}</h2>
              <div class="mb-4 text-body-2" v-if="presentation.shortDescription">
                {{ presentation.shortDescription }}
              </div>
              <div class="mb-6" style="white-space: pre-wrap">{{ presentation.description }}</div>

              <v-row class="mb-4" dense>
                <v-col cols="12" md="6">
                  <strong>Level:</strong> {{ presentation.level || '-' }}
                </v-col>
                <v-col cols="12" md="6">
                  <strong>Language:</strong> {{ presentation.language || '-' }}
                </v-col>
                <v-col cols="12" md="6">
                  <strong>Workshop:</strong> {{ presentation.isWorkshop ? 'Yes' : 'No' }}
                </v-col>
                <v-col cols="12" md="6" v-if="presentation.isWorkshop">
                  <div><strong>Free:</strong> {{ presentation.isFree ? 'Yes' : 'No' }}</div>
                  <div v-if="presentation.expectedPrice != null"><strong>Expected price:</strong> {{ presentation.expectedPrice }} PLN</div>
                  <div v-if="presentation.durationInMinutes != null"><strong>Duration:</strong> {{ presentation.durationInMinutes }} min</div>
                  <div v-if="presentation.maxGroupSize != null"><strong>Max group size:</strong> {{ presentation.maxGroupSize }}</div>
                </v-col>
                <v-col cols="12">
                  <strong>Tags:</strong>
                  <v-chip
                    v-for="tag in (presentation.tags || [])"
                    :key="String(tag)"
                    class="ma-1"
                    size="small"
                    color="primary"
                    variant="tonal"
                  >{{ String(tag) }}</v-chip>
                </v-col>
              </v-row>

              <h3 class="mt-6 mb-2">Speakers</h3>
              <div v-if="(presentation.speakers && presentation.speakers.length) || cospeakers.length">
                <v-row>
                  <!-- Names from presentation payload -->
                  <v-col cols="12" v-if="presentation.speakers && presentation.speakers.length">
                    <div class="text-subtitle-2 mb-2">Declared speakers:</div>
                    <div>
                      {{ presentation.speakers.map(s => s.name).join(', ') }}
                    </div>
                  </v-col>

                  <!-- Detailed bios from cospeakers endpoint -->
                  <v-col cols="12" md="6" v-for="sp in cospeakers" :key="sp.id">
                    <v-card variant="tonal" class="mb-3">
                      <v-card-title class="py-3">
                        <div class="d-flex align-center">
                          <v-avatar v-if="sp.photo" size="40" class="mr-3">
                            <v-img :src="sp.photo" alt="" />
                          </v-avatar>
                          <div>
                            <div class="text-subtitle-1">{{ sp.name }}</div>
                            <div class="text-caption" v-if="sp.www || sp.twitter || sp.github">
                              <a v-if="sp.www" :href="sp.www" target="_blank">Website</a>
                              <span v-if="sp.twitter"> • @{{ sp.twitter }}</span>
                              <span v-if="sp.github"> • {{ sp.github }}</span>
                            </div>
                          </div>
                        </div>
                      </v-card-title>
                      <v-card-text>
                        <div style="white-space: pre-wrap" v-if="sp.bio">{{ sp.bio }}</div>
                        <div v-else class="text-medium-emphasis">No bio provided.</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
                <div class="text-caption text-medium-emphasis" v-if="!cospeakers.length">
                  Bios are not available for the listed speakers in this view.
                </div>
              </div>
              <div v-else class="text-medium-emphasis">No speakers assigned.</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.preview h2 {
  margin-top: 0.5rem;
}
</style>
