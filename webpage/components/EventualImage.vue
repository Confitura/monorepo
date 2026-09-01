<template>
  <div class="container">
    <img :src="fullSizeSrc" ref="image" class="image" loading="lazy"/>
  </div>
</template>

<script setup lang="ts">

const { src, size = 90 } = defineProps<{ src?: string; size?: number }>()

let fullSizeSrc = computed(() => photoUrl(src, size))
</script>

<style scoped lang="scss">
.container {
  overflow: hidden;
  width: 100%;
  height: 100%;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(0);
  transition: 0.2s filter linear;
  will-change: filter;
}

.thumbnail {
  filter: blur(10px);
}
</style>
