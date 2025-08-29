<template>
  <div class="news" v-if="content">
    <PageHeader title="News" type="peace"/>
    <Box class="latest-news"
         :class="{'latest-news-odd' : $index % 2 === 0}"
         v-for="(content, $index) in content.all">
      <div class="latest-news-container">
        <div class="header">
          <h2>{{ content.title }}</h2>
          <small>{{ formatDate(content.publishedAt) }}</small>
        </div>
        <div class="main-info">
          <div class="body" v-html="content.body"></div>
        </div>
      </div>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
const content = await useArchiveFetch(`/news.json`)
    .then(response => response.data.value)

function formatDate(date: string) {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  };
  return new Date(date).toLocaleDateString('en-US', options);
}

const title = 'News — Confitura 2025';
const description = 'Follow the latest news about Confitura 2025: tickets, schedule, venue, and more.';
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'website'},
    {name: 'twitter:card', content: 'summary'},
    {name: 'twitter:title', content: title},
    {name: 'twitter:description', content: description}
  ]
})
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.news {
  overflow: hidden;
}

.content {
  .foreword {
    text-align: left;
    color: #000000;
    font-size: 1.5rem;
    @include md() {
      width: 50%;
    }

    &__link {
      color: $brand;
    }
  }
}

.latest-news-odd {
  background-color: black !important;

  .box__container {
    .latest-news-container {
      color: white;
      display: grid;
      grid-row-gap: 2rem;
      grid-template-areas: "h" "mi";
      align-content: center;
      @include lg() {
        grid-column-gap: 8rem;
        grid-row-gap: 52px;
        grid-template-columns: 1fr;
        grid-template-areas: "h" "mi";
      }
    }
  }
}

.header {
  grid-area: h;
  font-family: $font-bold;
  display: flex;
  align-items: center;
  gap: 2rem;

  h2 {
    font-size: 3rem;
    color: $brand;
  }

  small {
    font-size: 1.5rem;
  }
}

.formatDate {
  grid-area: t;
  font-family: $font-bold;
  font-size: 3rem;
  color: $brand;
}

.main-info {
  grid-area: mi;
  font-size: 1.75rem;
}

.additional-info {
  grid-area: ai;
  font-family: $font-bold;
  font-size: 1.75rem;
  color: $brand;
}

.main-info {
  .body {
    :deep(a) {

      &:link, &:visited, &:hover, &:active, &:-webkit-any-link {
        color: $brand !important;
      }
    }
  }
}


a:-webkit-any-link {
  color: #ee1f46 !important;
}
</style>
