<template>
  <Box color="red" class="latest-news" v-if="content && content.latest">
    <div class="latest-news-container">
      <h2 class="header">{{ content.latest.title }}</h2>
      <div class="main-info">
        <div class="body" v-html="content.latest.body"></div>
      </div>
    </div>
  </Box>
</template>

<script setup lang="ts">
const content = await useArchiveFetch(`/news.json`).then(response => response.data.value)
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

.latest-news {
  background-color: black !important;
}

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

.header {
  grid-area: h;
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
