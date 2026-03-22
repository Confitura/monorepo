<template>
  <div class="stars" ></div>
</template>
<script setup lang="ts">
</script>
<style scoped lang="scss">
@use "~/assets/fonts" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

.stars {
  z-index: 1;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  display: block;
  background-color: black;
  object-fit: cover;
}
</style>
