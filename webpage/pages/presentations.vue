<template>
  <div class="presentations">
    <PageHeader title="Presentations" type="coder" />
    <Box
      v-for="(presentation, $index) in presentations"
      :key="presentation.id"
      class="presentation"
      :class="{ 'presentation--odd': odd($index) }"
      color="white"
      :full="false"
    >
      <PresentationBox
        :presentation="presentation"
        :class="{ 'presentationBox--odd': odd($index) }"
      ></PresentationBox>
    </Box>

    <Contact />
  </div>
</template>

<script setup lang="ts">

import { useAPIFetch } from '~/composables/useAPIFetch'

let { data: presentations } = useAPIFetch('/api/presentations/search/accepted', {
  params: { projection: 'inlineSpeaker' },
  transform: (data) => data._embedded.presentations
})

function odd($index: number) {
  return $index % 2 !== 0
}

</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.presentations {
  overflow: hidden;
}

.presentation {
  &--odd {
    background-color: $brand;
    color: #ffffff;
  }

  &__header {
    color: $brand;

    &--odd {
      color: #ffffff;
    }
  }

  &__infoGroup {
    display: flex;
    flex-direction: column;
    @include md() {
      align-items: center;
      flex-direction: row;
    }
  }

  &__title {
    font-weight: bold;
    font-size: 2rem;
    margin-top: 0;
  }

  &__description {
    font-size: 1.2rem;
    line-height: 1.5rem;
    word-wrap: break-word;
    @include md() {
      font-size: 1.5rem;
      line-height: 1.7rem;
      padding-left: 4rem;
    }
  }
}
</style>
