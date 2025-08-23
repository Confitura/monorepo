<template>
  <div class="container">
    <img :src="fullSizeSrc" ref="image" class="image" />
  </div>
</template>

<script setup lang="ts">

const { src } = defineProps<{ src: string }>()
let loaded = false

let fullSizeSrc = computed(() => resizeTo(90))
let thumbnailSrc = computed(() => resizeTo(5))

function resizeTo(size: number) {
  if (src.includes('/photos/')) {
    return src//.replace('/photos/', `/photos/${size}/`) TODO
  } else if (src.includes('githubusercontent.com')) {
    return `${src}&s=${size}`
  } else if (src.includes('gravatar.com')) {
    return src.replace('s=300', `s=${size}`)
  } else {
    return src
  }
}
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
