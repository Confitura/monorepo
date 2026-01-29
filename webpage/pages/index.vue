<template>
  <div class="home__container">
    <HomeMainBanner/>
    <HomeLatestNews></HomeLatestNews>
    <HomeNumbers/>
    <HomeAbout/>
    <HomeInvitePartners/>
    <Contact/>
  </div>
</template>

<script setup lang="ts">

const title = 'Confitura 2026 — Java Conference in Warsaw';
const description = 'Join the largest Java community conference in Poland. Talks, workshops, and networking in Warsaw — Confitura 2026.';

useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
  ]
});
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
