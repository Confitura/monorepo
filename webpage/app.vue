<template>
  <TheHeader />
  <SocialLinks class="standard" />
  <NuxtPage />
</template>

<script setup lang="ts">
import 'modern-normalize'
import { useThemeStore } from '~/stores/themeStore'

const themeStore = useThemeStore()

onMounted(() => {
  resizedCallback()
  window.addEventListener('resize', resizedCallback)
  //TODO this.$store.commit(TOKEN, { token: localStorage.getItem(TOKEN) })
})

onUnmounted(() => {
  window.removeEventListener('resize', resizedCallback)
})

let resizedCallback = () => {
  const width = window.innerWidth
  themeStore.resizeWindow({ width })
}
</script>
<style lang="scss">
@use 'modern-normalize/modern-normalize.css';
@use "~/assets/styles.scss";

</style>