<template>
  <div class="agenda-flow-preview">
    <PageHeader title="Preview: agenda flow" type="planets" small />
    <AgendaFlow />
    <Contact />
  </div>
</template>

<script setup lang="ts">
// Unlisted page for reviewing the AgendaFlow component before it goes on a real page.
const title = 'Preview: how we build the agenda — Confitura'
useHead({
  title,
  meta: [{ name: 'robots', content: 'noindex, nofollow' }],
})
</script>

<style lang="scss" scoped>
.agenda-flow-preview {
  overflow: hidden;
}
</style>
