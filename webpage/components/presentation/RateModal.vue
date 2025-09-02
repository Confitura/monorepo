<script setup lang="ts">
const { presentationId } = defineProps<{ presentationId: string }>()
const url = computed(() => presentationId ? `http://localhost:5173/rate?entryId=${encodeURIComponent(presentationId)}` : null)
</script>

<template>
  <Modal v-if="presentationId" @close="$emit('close')">
    <div class="rateModal">
      <iframe v-if="url" class="rateModal__iframe" :src="url" frameborder="0" allowfullscreen></iframe>
      <div v-else class="rateModal__error">Missing presentation id</div>
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