<script setup lang="ts">

definePage({
  meta: {
    skipMenu: true,
    layout: 'no-distractions',
  },
})

import {ref, computed, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import {publishedApi} from '@/utils/api.ts'
import type {
  InlinePresentationWithSpeakers,
  PublicSpeaker
} from '@/utils/api-axios-client'

// Accept either path param or query param for flexibility: /rate/123 or /rate?entryId=123
const route = useRoute()
const entryId = computed(() => (route.params.entryId as string) || (route.query.entryId as string) || '')

const rating = ref<number | null>(null)
const comment = ref('')
const submitted = ref(false)
const loading = ref(false)

const presLoading = ref(false)
const presError = ref<string | null>(null)
const presentation = ref<InlinePresentationWithSpeakers | null>(null)
const cospeakers = ref<PublicSpeaker[]>([])

async function loadPresentation() {
  presLoading.value = true
  presError.value = null
  presentation.value = null
  cospeakers.value = []
  try {
    const list = await publishedApi.acceptedPresentations()
    const ws = await publishedApi.acceptedWorkshops()
    const items = [...(list.data || []), ...(ws.data || [])] as InlinePresentationWithSpeakers[]
    presentation.value = items.find(p => p.id === entryId.value) || null
  } catch (e) {
    console.error(e)
    presError.value = 'Failed to load presentation details.'
  }
  try {
    if (entryId.value) {
      // PublishedController exposes /published/users/{id}/public for a single speaker id.
      // Our InlinePresentationWithSpeakers has speakers array with id/name. Fetch detailed public profiles for avatars if needed.
      const sps = presentation.value?.speakers || []
      const results: PublicSpeaker[] = []
      for (const s of sps) {
        try {
          const res = await publishedApi.speaker(String(s.id))
          if (res.data) results.push(res.data as unknown as PublicSpeaker)
        } catch (e) {
          // ignore missing
        }
      }
      cospeakers.value = results
    }
  } catch (e) {
    console.warn('Cospeakers not available', e)
  } finally {
    presLoading.value = false
  }
}

function submit() {
  if (!entryId.value) return
  loading.value = true
  // For now, just simulate submit as backend API is not specified in the task
  setTimeout(() => {
    submitted.value = true
    loading.value = false
  }, 600)
}

onMounted(() => {
  if (entryId.value) {
    loadPresentation()
  }
})

let votingEnabled = false
</script>

<template>
  <v-app :theme="'light'">
    <v-main style="height: 100%">
      <v-container style="padding: 0; height: 100%">
        <v-card style="height: 100%">
          <v-toolbar density="comfortable" color="primary"
                     :title="presentation?.title || ''"/>
          <v-card-text>
            <div v-if="!entryId" class="pa-4">
              <v-alert type="error" title="Missing entryId"
                       text="No presentation/workshop identifier provided."/>
            </div>
            <div v-else>
              <!-- Presentation details -->
              <div class="mb-4" v-if="presLoading">
                <v-progress-circular indeterminate color="primary" size="24"/>
              </div>
              <div v-else-if="presError">
                <v-alert type="warning" :text="presError" class="mb-4"/>
              </div>
              <div v-else-if="presentation" class="mb-6">
                <div class="d-flex align-center flex-wrap mb-2"
                     v-if="(cospeakers && cospeakers.length) || (presentation.speakers && presentation.speakers.length)">
                  <template v-if="cospeakers && cospeakers.length">
                    <div v-for="sp in cospeakers" :key="sp.id"
                         class="d-flex align-center mr-4 mb-2">
                      <v-avatar v-if="sp.photo" size="28" class="mr-2">
                        <v-img :src="sp.photo" alt=""/>
                      </v-avatar>
                      <span class="text-body-2">{{ sp.name }}</span>
                    </div>
                  </template>
                  <template v-else>
                    <span class="text-body-2">{{
                        presentation.speakers.map(s => s.name).join(', ')
                      }}</span>
                  </template>
                </div>
                <div>
                  <v-chip v-for="tag in (presentation.tags || [])"
                          :key="tag.id" class="ma-1" size="x-small"
                          color="primary" variant="tonal">{{ tag.name }}
                  </v-chip>
                </div>
              </div>


              <div class="" style="white-space: pre-wrap">
                {{ presentation?.description }}
              </div>

              <v-banner text="Rating will be enabled on conference day"
                        v-if="!votingEnabled">
              </v-banner>
              <v-container v-if="votingEnabled">
                <div class="mb-6">
                  <div class="mb-2">Your rating</div>
                  <v-rating v-model="rating" :length="5" color="amber" hover
                            disabled
                            clearable size="32"/>
                </div>

                <div class="mb-6">
                  <div class="mb-2">Comment (optional)</div>
                  <v-textarea v-model="comment" auto-grow rows="3"
                              disabled
                              placeholder="What did you like? What can be improved?"/>
                </div>

                <v-alert v-if="submitted" type="success" variant="tonal"
                         class="mb-4" text="Thanks for your feedback!"/>

                <v-btn color="primary" :disabled="!rating || loading"
                       :loading="loading" @click="submit">
                  Submit
                </v-btn>
              </v-container>
            </div>
          </v-card-text>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
:root {
  color-scheme: light;
}

html, body, #app {
  background: transparent;
}
</style>
