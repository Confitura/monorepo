<script setup lang="ts">
import {computed} from 'vue'

interface Tag {
  id: string | number;
  name: string
}

interface Presentation {
  language?: string
  level?: string
  workshop?: boolean
  // workshop details
  isFree?: boolean | null
  expectedPrice?: number | null
  durationInMinutes?: number | null
  maxGroupSize?: number | null
  tags?: Tag[]
}

const props = withDefaults(defineProps<{ presentation: Presentation; showTags?: boolean }>(), {
  showTags: true,
})

const hasTags = computed(() => !!props.presentation && (props.presentation.tags?.length || 0) > 0)

const language = computed(() => {
  const lang = props.presentation?.language === 'en' ? 'english' : 'polish'
  return lang
})
</script>

<template>
  <div class="presentationMetadata">
    <div class="presentationMetadata__group">
      <i class="presentationMetadata__icon fas fa-flag" title="language"></i>
      <div class="presentationMetadata__language">{{ props.presentation.language }}</div>
    </div>
    <div class="presentationMetadata__group">
      <i class="presentationMetadata__icon fas fa-graduation-cap" title="level"></i>
      <div class="presentationMetadata__level">{{ props.presentation.level }}</div>
    </div>
    <div class="presentationMetadata__group" v-if="props.presentation.workshop">
      <i class="presentationMetadata__icon fas fa-hammer" title="workshop"></i>
      <div class="presentationMetadata__workshop">workshop</div>
    </div>
    <div class="presentationMetadata__group" v-if="props.presentation.workshop">
      <i class="presentationMetadata__icon fas fa-clock" title="workshop duration"></i>
      <div class="presentationMetadata__duration">{{ props.presentation.durationInMinutes }} min</div>
    </div>
    <div class="presentationMetadata__group" v-if="!props.presentation.workshop">
      <i class="presentationMetadata__icon fas fa-microphone" title="presentation"></i>
      <div class="presentationMetadata__presentation">presentation</div>
    </div>
    <div class="presentationMetadata__group" v-if="props.showTags">
      <template v-if="hasTags">
        <i class="presentationMetadata__icon  fas fa-tags" title="tags"></i>
        <div class="presentationMetadata__tags">
          <span class="presentationMetadata__tag" v-for="tag in props.presentation.tags"
                :key="tag.id">{{ tag.name }}</span>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.presentationMetadata {
  font-weight: bold;
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  font-size: 1.5rem;
  margin-bottom: 2rem;
  @include md() {
    flex-direction: row;
  }

  &__group {
    display: flex;
    margin-bottom: 0.5rem;
  }

  &__icon {
    margin-right: 1rem;
  }

  &__language,
  &__level,
  &__duration,
  &__workshop,
  &__presentation {
    margin-right: 1rem;
  }

  &__tag {
    &:not(:last-child)::after {
      content: ", ";
    }
  }
}
</style>