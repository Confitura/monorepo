<template>
  <div class="news-entry" v-if="entry">
    <PageHeader title="News" type="peace"/>
    <Box class="news-entry__box" color="white">
      <article>
        <h2>{{ entry.title }}</h2>
        <small class="news-entry__date">{{ formatNewsDate(entry.publishedAt) }}</small>
        <img v-if="entry.cover" class="news-cover" :src="photoUrl(entry.cover, 1200)" :alt="entry.title"/>
        <div class="news-entry__body" v-html="renderMarkdown(entry.body)"></div>
      </article>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
const slug = useRoute().params.slug as string
const news = await useArchiveFetch(`/news.json`).then(response => response.data.value)
// No feed at all means the fetch failed: answer 503 (retry later), not a 404 that scrapers may cache.
if (!news) {
  throw createError({statusCode: 503, statusMessage: 'News is unavailable', fatal: true})
}
const entry = news.all?.find((candidate: { slug: string }) => candidate.slug === slug)
if (!entry) {
  throw createError({statusCode: 404, statusMessage: 'News entry not found', fatal: true})
}

const title = `${entry.title} — Confitura`
const description = plainText(entry.body)
const url = `https://confitura.pl/news/${entry.slug}`
// Without a cover the site-wide share image (and its 1200x630 size tags) stays; a cover's size is unknown.
const coverMeta = entry.cover ? [
  {property: 'og:image', content: entry.cover},
  {property: 'og:image:width', content: undefined},
  {property: 'og:image:height', content: undefined},
  {name: 'twitter:image', content: entry.cover}
] : []
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
    {name: 'twitter:description', content: description},
    ...coverMeta
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

  .news-cover {
    display: block;
    max-width: 100%;
    margin: 2rem 0;
  }

  &__body {
    font-size: 1.75rem;

    :deep(a:link), :deep(a:visited), :deep(a:hover), :deep(a:active) {
      color: $brand;
    }
  }
}
</style>
