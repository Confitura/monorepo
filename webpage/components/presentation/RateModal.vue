<script setup lang="ts">
import { useArchiveFetch } from '~/composables/useAPIFetch'
import { watch, ref, onMounted, onBeforeUnmount } from 'vue'

const { presentationId } = defineProps<{ presentationId: string | null }>()
const presentation = useState('rate_presentation', () => null as any)

const config = useRuntimeConfig();
const appUrl = config.public.appUrl
const url = computed(() => presentationId ? `${appUrl}/rate?entryId=${encodeURIComponent(presentationId)}` : null)

// The /rate page (cross-origin iframe) reports its content height so the modal
// fits it — small for a "disabled"/"conference day" banner, tall for the form.
// Starts small and grows only when a height is reported.
const iframeHeight = ref(80)

function onMessage(event: MessageEvent) {
  if (event.origin !== appUrl) return
  const data = event.data
  if (data?.type !== 'confitura:rate-height') return
  const height = Number(data.height)
  if (Number.isFinite(height) && height > 0) {
    iframeHeight.value = Math.min(Math.ceil(height), 2000)
  }
}

onMounted(() => window.addEventListener('message', onMessage))
onBeforeUnmount(() => window.removeEventListener('message', onMessage))

// Load all accepted presentations and workshops
const { data: presentations } = await useArchiveFetch('/presentations/accepted.json', {
  transform: (data) => data
})
const { data: workshops } = await useArchiveFetch('/workshops/accepted.json', {
  transform: (data) => data
})

const findById = (id?: string | null) => {
  if (!id) return null
  const inPresentations = presentations?.value?.find((p: any) => p.id === id) || null
  if (inPresentations) return inPresentations
  return workshops?.value?.find((w: any) => w.id === id) || null
}

watch(
  () => [presentationId, presentations?.value, workshops?.value],
  () => {
    presentation.value = findById(presentationId)
  },
  { immediate: true }
)
</script>

<template>
  <Modal v-if="presentationId" fit-content @close="$emit('close')">
    <div class="rateModal">
      <PresentationBox v-if="presentation" :presentation="presentation">
        <iframe v-if="url" class="rateModal__iframe" :style="{ height: iframeHeight + 'px' }" :src="url" frameborder="0" allowfullscreen></iframe>
        <div v-else class="rateModal__error">Missing presentation details</div>
      </PresentationBox>
    </div>
  </Modal>
</template>

<style scoped lang="scss">
.rateModal {
}

.rateModal__iframe {
  width: 100%;
  border: none;
  display: block;
  transition: height 0.2s ease;
}

.rateModal__error {
  padding: 2rem;
}
</style>