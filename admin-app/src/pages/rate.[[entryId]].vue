<script setup lang="ts">

definePage({
  meta: {
    skipMenu: true,
    layout: 'no-distractions',
  },
})
import {v4 as uuidv4} from 'uuid';
import {ref, computed, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import {presentationApi, publishedApi} from '@/utils/api.ts'
import {
  type InlinePresentationWithSpeakers,
  type PublicSpeaker, RateValueEnum
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

  let ratingToken = localStorage.getItem('ratingToken') || uuidv4()
  localStorage.setItem('ratingToken', ratingToken)

  loading.value = true
  presentationApi.addRating(entryId.value, {
    reviewerToken: ratingToken,
    value: rating.value,
    comment: comment.value
  })
    .then((response) => {
      if (response.status >= 200 && response.status <= 300) {
        submitted.value = true
      } else {
        presError.value = 'Failed to submit rating. Please try again later.'
      }
    })
    .finally(() => {
      loading.value = false
    })

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

const labels = ref(['terrible', 'bad', 'it was fine', 'great', 'awesome'])
</script>

<template>
  <v-app :theme="'light'">
    <v-main style="height: 100%">
      <v-container style="padding: 0; height: 100%">
        <v-card style="height: 100%">
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

            <v-banner text="Rating will be enabled on conference day"
                      v-if="!votingEnabled">
            </v-banner>
            <v-container v-if="votingEnabled">
              <div class="mb-6">
                <div class="mb-2">Your rating</div>
                <v-rating v-model="rating"
                          length="5"
                          color="amber"
                          hover
                          :item-labels="labels"
                          clearable size="50">
                </v-rating>
              </div>

              <div class="mb-6">
                <div class="mb-2">Comment (optional)</div>
                <v-textarea v-model="comment" auto-grow rows="3"
                            placeholder="What did you like? What can be improved?"/>
              </div>

              <v-alert v-if="submitted" type="success" variant="tonal"
                       class="mb-4" text="Thanks for your feedback!"/>

              <v-btn color="primary" :disabled="!rating || loading"
                     :loading="loading" @click="submit">
                Submit {{ rating }}
              </v-btn>
            </v-container>
          </div>
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
