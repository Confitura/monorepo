<script setup lang="ts">

import {v4 as uuidv4} from 'uuid';
import {ref, computed, onMounted, onBeforeUnmount} from 'vue'
import {useRoute} from 'vue-router'
import {addRating, getRatingEnabled, getRatingStatus} from '@/utils/api.ts'


definePage({
  meta: {
    skipMenu: true,
    layout: 'no-distractions',
  },
})

// Accept either path param or query param for flexibility: /rate/123 or /rate?entryId=123
const route = useRoute()
const entryId = computed(() => ((route.params as { entryId?: string }).entryId as string) || (route.query.entryId as string) || '')

const rating = ref<number | undefined>(undefined)
const comment = ref('')
const submitted = ref(false)
const loading = ref(false)

const presError = ref<string | null>(null)

// Whether rating is enabled for this specific presentation (admin-controlled).
// Defaults to true; the /rate form only shows when the global window is open AND this is true.
const ratingEnabled = ref(true)

// Global rating window, derived from the agenda: open once the conference's first
// day has arrived. `ratingOpensAt` (ISO date) is shown in the "not open yet" banner.
const globalRatingOpen = ref(false)
const ratingOpensAt = ref<string | null>(null)

async function loadPresentation() {
  presError.value = null
  if (!entryId.value) return
  try {
    const res = await getRatingEnabled({ path: { presentationId: entryId.value } })
    ratingEnabled.value = res.data?.ratingEnabled ?? true
  } catch (_) {
    // leave the default; the global gate still applies
  }
}

async function loadRatingStatus() {
  try {
    const res = await getRatingStatus()
    globalRatingOpen.value = res.data?.open ?? false
    ratingOpensAt.value = res.data?.opensAt ?? null
  } catch (_) {
    // stay closed on error
  }
}

const opensAtLabel = computed(() => {
  if (!ratingOpensAt.value) return 'Rating is disabled on this presentation'
  const date = new Date(ratingOpensAt.value)
  const formatted = Number.isNaN(date.getTime())
    ? ratingOpensAt.value
    : date.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })
  return `Rating will be enabled on ${formatted}`
})

function submit() {
  if (!entryId.value) return

  const ratingToken = localStorage.getItem('ratingToken') || uuidv4()
  localStorage.setItem('ratingToken', ratingToken)

  loading.value = true
  addRating({
    path: { presentationId: entryId.value },
    body: {
      reviewerToken: ratingToken,
      value: rating.value ?? undefined,
      comment: comment.value
    }
  })
    .then((res) => {
      if ((res.status ?? 0) >= 200 && (res.status ?? 0) < 300) {
        submitted.value = true
      } else {
        presError.value = 'Failed to submit rating. Please try again later.'
      }
    })
    .finally(() => {
      loading.value = false
    })
}

// When embedded in the webpage modal (cross-origin iframe), report our real
// content height so the parent can size the iframe to fit — a one-line banner
// stays small, the full form gets the space it needs.
let resizeObserver: ResizeObserver | null = null

function reportHeight() {
  if (typeof window === 'undefined' || window.parent === window) return
  const height = Math.ceil(document.documentElement.scrollHeight)
  window.parent.postMessage({ type: 'confitura:rate-height', height }, '*')
}

onMounted(() => {
  loadRatingStatus()
  if (entryId.value) {
    loadPresentation()
  }
  if (typeof window !== 'undefined' && window.parent !== window) {
    reportHeight()
    if ('ResizeObserver' in window) {
      resizeObserver = new ResizeObserver(() => reportHeight())
      resizeObserver.observe(document.body)
    }
  }
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
})

const votingEnabled = computed(() => globalRatingOpen.value && ratingEnabled.value)

const labels = ref(['terrible', 'bad', 'it was fine', 'great', 'awesome'])
</script>

<template>
  <v-app :theme="'light'">
    <v-main>
      <v-container style="padding: 0">
        <v-card>
          <div v-if="!entryId" class="pa-4">
            <v-alert
type="error" title="Missing entryId"
                     text="No presentation/workshop identifier provided."/>
          </div>
          <div v-else>
            <!-- Presentation details -->
            <div v-if="presError">
              <v-alert type="warning" :text="presError" class="mb-4"/>
            </div>

            <v-banner
v-if="!ratingEnabled"
                      text="Rating is disabled for this session"/>
            <v-banner
v-else-if="!votingEnabled"
                      :text="opensAtLabel"/>
            <v-container v-if="votingEnabled">
              <div class="mb-6">
                <div class="mb-2">Your rating</div>
                <v-rating
v-model="rating"
                          length="5"
                          color="amber"
                          hover
                          :item-labels="labels"
                          clearable size="50"/>
              </div>

              <div class="mb-6">
                <div class="mb-2">Comment (optional)</div>
                <v-textarea
v-model="comment" auto-grow rows="3"
                            placeholder="What did you like? What can be improved?"/>
              </div>

              <v-alert
v-if="submitted" type="success" variant="tonal"
                       class="mb-4" text="Thanks for your feedback!"/>

              <v-btn
color="primary" :disabled="!rating || loading"
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
