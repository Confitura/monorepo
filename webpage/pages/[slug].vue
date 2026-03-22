<template>
  <div class="faq">
    <PageHeader title="Not Found" />
    <Box class="content" color="white">
      <p class="foreword">
        oops, nothing to see here
      </p>
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">
const title = 'Page Not Found — Confitura';
const description = 'Oops, nothing to see here. The page you are looking for could not be found.';
useHead({
  title,
  meta: [
    { name: 'robots', content: 'noindex, nofollow' },
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
  ]
})
</script>

<style lang="scss" scoped>
</style>
