<template>
  <div class="terms">
    <PageHeader title="Terms and Conditions" />
    <Box class="content" color="white">
      <PageFragment name="terms-and-conditions" class="terms__info" />
    </Box>
    <Contact />
  </div>
</template>

<script setup>
const title = 'Terms and Conditions — Confitura 2026';
const description = 'Read the terms and conditions for participating in Confitura 2026.';
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
