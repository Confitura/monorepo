<template>
  <div class="speakers">
    <PageHeader title="Our speakers" type="coder" />
    <Box class="content no-padding" color="white">
      <UsersGrid :users="speakers" class="speakers__grid" />
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">

let { data: speakers } = useAPIFetch('/api/users/search/speakers', {
  transform: (data) => data._embedded.publicUsers
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
