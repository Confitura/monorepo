<template>
  <div class="presentationBox" v-if="presentation">
    <div class="presentationBox__header">
      <h2 class="presentationBox__title">{{ presentation.title }}</h2>
      <div class="presentationBox__infoGroup">
        <PresentationSpeakers :speakers="presentation.speakers"></PresentationSpeakers>
        <PresentationMetadata class="presentationBox__metadata" :presentation="presentation"></PresentationMetadata>
      </div>
    </div>
    <div class="presentationBox__description" v-html="description"></div>
  </div>
</template>

<script setup lang="ts">
import {marked} from 'marked'

const {presentation} = defineProps<{ presentation }>()
let description = computed(() => marked(presentation.description))

</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.presentationBox--odd {
  .presentationBox__header {
    color: #ffffff;
  }
}

.presentationBox {
  &__header {
    color: $brand;
  }

  &__infoGroup {
    display: flex;
    flex-direction: column;
    @include md() {
      align-items: center;
      flex-direction: row;
      flex-wrap: wrap;
      column-gap: 2rem;
    }
  }

  &__metadata {
    margin-top: 1rem;
    @include md() {
      flex: 1 1 100%;
      margin-top: 0.5rem;
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
      padding-left: 4rem;
      line-height: 1.7rem;
    }
  }
}
</style>
