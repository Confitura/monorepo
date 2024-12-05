<template>
  <div class="home__container">
    <HomeMainBanner />
    <HomeAbout />
    <HomeNumbers />
    <HomePartners />
    <Contact />
  </div>
</template>

<script setup lang="ts">
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
