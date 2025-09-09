<template>
  <Modal @close="$emit('close')" v-if="presentation">
    <PresentationBox :presentation="presentation"></PresentationBox>
  </Modal>
</template>

<script setup lang="ts">

const {presentationId} = defineProps<{ presentationId: string }>()
const presentation = useState('presentation', () => null as any)

// Load all accepted presentations (same source as pages/presentations.vue)
const {data: presentations} = await useArchiveFetch('/presentations/accepted.json', {
  transform: (data) => data
})

const findById = (id?: string | null) => {
  if (!id || !presentations?.value) return null
  return presentations.value.find((p: any) => p.id === id) || null
}

// Initialize and react to changes
watch(
    () => [presentationId, presentations?.value],
    () => {
      presentation.value = findById(presentationId)
    },
    {immediate: true}
)
</script>

<style scoped lang="scss"></style>
