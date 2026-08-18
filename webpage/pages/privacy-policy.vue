<template>
  <div class="terms">
    <PageHeader title="Privacy Policy" />
    <Box class="content" color="white">
      <PageFragment class="questions" name="privacy-policy" />
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">
const title = 'Privacy Policy — Confitura';
const description = 'Read the Confitura privacy policy to learn how we collect, use, and protect your personal data.';
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
})
</script>

<style lang="scss">
@use "~/assets/colors" as *;

.terms__info {
  font-size: 1.5rem;
  line-height: 2rem;

  a {
    color: $brand;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .table {
    width: 100%;
    border-collapse: collapse;

    td,
    th {
      padding: 0.75rem;
      border-bottom: 1px #dee2e6 solid;
      box-sizing: border-box;
    }

    tr {
      box-sizing: border-box;
    }
  }
}
</style>
