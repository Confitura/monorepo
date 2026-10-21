<template>
  <div class="news-entry" v-if="entry">
    <PageHeader title="News" type="peace"/>
    <Box class="news-entry__box" color="white">
      <article>
        <h2>{{ entry.title }}</h2>
        <small class="news-entry__date">{{ formatNewsDate(entry.publishedAt) }}</small>
        <div class="news-entry__body" v-html="renderMarkdown(entry.body)"></div>
      </article>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
const slug = useRoute().params.slug as string
const news = await useArchiveFetch(`/news.json`).then(response => response.data.value)
const entry = news?.all?.find((candidate: { slug: string }) => candidate.slug === slug)
if (!entry) {
  throw createError({statusCode: 404, statusMessage: 'News entry not found', fatal: true})
}

const title = `${entry.title} — Confitura`
const description = plainText(entry.body)
const url = `https://confitura.pl/news/${entry.slug}`
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: entry.title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'article'},
    {property: 'og:url', content: url},
    {name: 'twitter:card', content: 'summary_large_image'},
    {name: 'twitter:title', content: entry.title},
    {name: 'twitter:description', content: description}
  ]
})
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;

.news-entry {
  overflow: hidden;

  h2 {
    font-family: $font-bold;
    font-size: 3rem;
    color: $brand;
  }

  &__date {
    font-size: 1.5rem;
  }

  &__body {
    font-size: 1.75rem;

    :deep(a:link), :deep(a:visited), :deep(a:hover), :deep(a:active) {
      color: $brand;
    }
  }
}
</style>
