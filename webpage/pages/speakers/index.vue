<template>
  <div class="speakers">
    <PageHeader title="Our speakers" type="coder"/>
    <Box class="content no-padding" color="white">
      <UsersGrid :users="speakers" class="speakers__grid"/>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">

useHead({
  title: 'Speakers | Confitura 2026',
  meta: [
    { name: 'description', content: 'Meet the speakers of Confitura 2026 — the biggest Java community conference in Poland.' },
    { property: 'og:title', content: 'Speakers | Confitura 2026' },
    { property: 'og:description', content: 'Meet the speakers of Confitura 2026 — the biggest Java community conference in Poland.' },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary_large_image' },
    { name: 'twitter:title', content: 'Speakers | Confitura 2026' },
    { name: 'twitter:description', content: 'Meet the speakers of Confitura 2026 — the biggest Java community conference in Poland.' },
  ],
})

let {data: speakers} = useArchiveFetch('/users/search/speakers.json', {
  transform: (data) => {
    const shuffleArray = (array) => {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
      return array;
    }
    return shuffleArray([...data]);
  }
})


</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.speakers {
  overflow: hidden;

  &__grid {
    margin-top: 4rem;
  }
}
</style>
