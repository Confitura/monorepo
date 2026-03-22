<template>
  <Modal @close="$emit('close')" v-if="presentation">
    <PresentationBox :presentation="presentation"></PresentationBox>
  </Modal>
</template>

<script setup lang="ts">

const {presentationId} = defineProps<{ presentationId: string }>()
const presentation = useState('presentation', () => null as any)

// Load all accepted presentations and workshops
const {data: presentations} = await useArchiveFetch('/presentations/accepted.json', {
  transform: (data) => data
})
const {data: workshops} = await useArchiveFetch('/workshops/accepted.json', {
  transform: (data) => data
})

const findById = (id?: string | null) => {
  if (!id) return null
  const inPresentations = presentations?.value?.find((p: any) => p.id === id) || null
  if (inPresentations) return inPresentations
  return workshops?.value?.find((w: any) => w.id === id) || null
}

// Initialize and react to changes
watch(
    () => [presentationId, presentations?.value, workshops?.value],
    () => {
      presentation.value = findById(presentationId)
    },
    {immediate: true}
)
</script>

<style scoped lang="scss"></style>
