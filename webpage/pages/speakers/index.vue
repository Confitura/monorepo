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
