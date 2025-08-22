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
  transform: (data) => data
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
