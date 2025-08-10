<template>
  <div class="home__container">
    <HomeMainBanner/>
    <HomeAbout/>
    <NlpDay/>
    <HomeNumbers/>
    <HomePartners/>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
import NlpDay from "~/components/home/NlpDay.vue";
</script>

<style lang="scss">
@supports not (-webkit-overflow-scrolling: touch) {
  .home__container {
    max-height: 100vh;
    overflow-y: scroll;
    scroll-snap-type: y proximity;

    & > section {
      scroll-snap-align: start;
    }
  }
}
</style>
