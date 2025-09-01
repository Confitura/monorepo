<template>
  <div class="video">
    <video
      class="video__player"
      src="/assets/videos/confitura_2025.mp4"
      title="Confitura 2025 teaser"
      autoplay
      muted
      loop
      playsinline
      controls
    >
      Sorry, your browser doesn't support embedded videos.
    </video>
  </div>
</template>
<style scoped lang="scss">
@use "~/assets/fonts" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

.video .video__player{
  width: 100%;
  height: auto;
  @include md() {
    max-width: 40vw;
  }
}
</style>
<script setup lang="ts">
</script>