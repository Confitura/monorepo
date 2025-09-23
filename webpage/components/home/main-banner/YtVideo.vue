<template>
  <div class="video">

    <iframe width="560" height="315" src="https://www.youtube.com/embed/FBbxbLVqlrQ?si=XEy5X0t1yhhNKROr" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
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