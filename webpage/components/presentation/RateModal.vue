<script setup lang="ts">
import { useArchiveFetch } from '~/composables/useAPIFetch'
import { watch } from 'vue'

const { presentationId } = defineProps<{ presentationId: string | null }>()
const presentation = useState('rate_presentation', () => null as any)

// Load all accepted presentations and filter by id
const { data: presentations } = await useArchiveFetch('/presentations/accepted.json', {
  transform: (data) => data
})

const findById = (id?: string | null) => {
  if (!id || !presentations?.value) return null
  return presentations.value.find((p: any) => p.id === id) || null
}

watch(
  () => [presentationId, presentations?.value],
  () => {
    presentation.value = findById(presentationId)
  },
  { immediate: true }
)

const config = useRuntimeConfig();
const appUrl = config.public.appUrl
const url = computed(() => presentationId ? `${appUrl}/rate?entryId=${encodeURIComponent(presentationId)}` : null)
</script>

<template>
  <Modal v-if="presentationId" @close="$emit('close')">
    <div class="rateModal">
      <PresentationBox v-if="presentation" :presentation="presentation" />
      <div v-else class="rateModal__error">Missing presentation details</div>
<!--      <iframe v-if="url" class="rateModal__iframe" :src="url" frameborder="0" allowfullscreen></iframe>-->
    </div>
  </Modal>
</template>

<style scoped lang="scss">
.rateModal {
  display: flex;
  flex-grow: 1;
}

.rateModal__iframe {
  width: 100%;
  height: 70vh;
  border: none;
}

.rateModal__error {
  padding: 2rem;
}
</style>